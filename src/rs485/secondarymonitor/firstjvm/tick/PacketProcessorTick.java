package rs485.secondarymonitor.firstjvm.tick;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.IHandlePacket;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class PacketProcessorTick implements ITickHandler, IHandlePacket {
	
	Queue<ConsolePacket> packets = new LinkedList<ConsolePacket>();
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		synchronized(packets) {
			while(!packets.isEmpty()) {
				ConsolePacket packet = packets.poll();
				ConsolePacketHandler.onPacketData(packet);
			}
		}
	}
	
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
	
	@Override
	public String getLabel() {
		return "PacketProcessor";
	}

	@Override
	public void handlePacket(DataInputStream data) throws IOException {
		int packetID = data.readInt();
		final ConsolePacket packet = ConsolePacketHandler.packetlist.get(packetID).template();
		packet.readData(data);
		if(packet.needMainThread()) {
			synchronized(packets) {
				packets.add(packet);
			}
		} else {
			new Thread() {
				public void run() {
					ConsolePacketHandler.onPacketData(packet);
				}
			}.start();
		}
	}
}
