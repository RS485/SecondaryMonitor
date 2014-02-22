package rs485.secondarymonitor.proxy;

import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.network.PacketHandler;
import rs485.secondarymonitor.network.abstractpackets.ModernPacket;
import rs485.secondarymonitor.network.packets.ConsolePacketWrapper;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class MainProxy {
	
	@SidedProxy(clientSide="rs485.secondarymonitor.proxy.ClientProxy", serverSide="rs485.secondarymonitor.proxy.ServerProxy")
	public static IProxy proxy;

	public static void sendPacketToServer(ModernPacket packet) {
		packet.create();
		PacketDispatcher.sendPacketToServer(packet.getPacket());
	}

	public static void sendPacketToPlayer(ModernPacket packet, Player player) {
		packet.create();
		PacketDispatcher.sendPacketToPlayer(packet.getPacket(), player);
	}
	
	public static void sendPacketToPlayer(ConsolePacket packet, Player player) {
		packet.create();
		ConsolePacketWrapper packetWrapper = PacketHandler.getPacket(ConsolePacketWrapper.class).setData(packet.getData());
		packetWrapper.create();
		PacketDispatcher.sendPacketToPlayer(packetWrapper.getPacket(), player);
	}
}
