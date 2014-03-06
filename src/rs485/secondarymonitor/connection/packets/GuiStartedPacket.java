package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.firstjvm.JVMHandler;

public class GuiStartedPacket extends ConsolePacket {
	
	public GuiStartedPacket(int id) {
		super(id);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {}
	
	@Override
	public void processPacket() {
		JVMHandler.instance().setJVMCompleted(true);
		JVMHandler.instance().setStarting(false);
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {}
	
	@Override
	public ConsolePacket template() {
		return new GuiStartedPacket(getId());
	}

	@Override
	public boolean needMainThread() {
		return false;
	}
}
