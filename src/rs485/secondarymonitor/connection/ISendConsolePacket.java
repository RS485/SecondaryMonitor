package rs485.secondarymonitor.connection;

import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;

public interface ISendConsolePacket {
	public void sendConsolePacket(ConsolePacket packet);
	public boolean isActive();
}
