package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.firstjvm.JVMHandler;

public class ShutdownPacket extends ConsolePacket {
	
	public ShutdownPacket(int id) {
		super(id);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {}
	
	@Override
	public void processPacket() {
		JVMHandler.instance().remoteShutdown();
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {}
	
	@Override
	public ConsolePacket template() {
		return new ShutdownPacket(getId());
	}

	@Override
	public boolean needMainThread() {
		return true;
	}
}
