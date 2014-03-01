package rs485.secondarymonitor.connection;

import java.io.DataInputStream;
import java.io.IOException;

public interface IHandlePacket {
	public void handlePacket(DataInputStream data) throws IOException;
}
