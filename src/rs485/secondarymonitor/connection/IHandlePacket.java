package rs485.secondarymonitor.connection;

import java.io.DataInputStream;

public interface IHandlePacket {
	public void handlePacket(DataInputStream data);
}
