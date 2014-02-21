package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;

public class KeepAlivePacket extends ConsolePacket {

	public KeepAlivePacket(int id) {
		super(id);
	}

	@Override
	public boolean needMainThread() {
		return false;
	}

	@Override
	public ConsolePacket template() {
		return new KeepAlivePacket(getId());
	}

	@Override
	public void readData(DataInputStream data) throws IOException {}
	
	@Override
	public void processPacket() {}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {}
	
}
