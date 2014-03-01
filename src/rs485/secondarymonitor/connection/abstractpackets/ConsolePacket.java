package rs485.secondarymonitor.connection.abstractpackets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain=true)
public abstract class ConsolePacket {
	
	@Getter
	private final int id;

	@Getter
	private byte[] data = null;

	public ConsolePacket(int id) {
		this.id = id;
	}
	
	public void create() {
		if(data != null) return; //PacketBuffer already created
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(bytes);
		try {
			dataStream.writeInt(getId());
			writeData(dataStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		data = bytes.toByteArray();
	}

	public abstract boolean needMainThread();
	public abstract void readData(DataInputStream data) throws IOException;
	public abstract void processPacket();
	public abstract void writeData(DataOutputStream data) throws IOException;
	public abstract ConsolePacket template();
}