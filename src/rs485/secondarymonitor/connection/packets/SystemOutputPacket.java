package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;

@Accessors(chain=true)
public class SystemOutputPacket extends ConsolePacket {
	
	public SystemOutputPacket(int id) {
		super(id);
	}

	@Getter
	@Setter
	private byte[] msg;
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		int length = data.readInt();
		setMsg(new byte[length]);
		data.read(getMsg());
	}
	
	@Override
	public void processPacket() {
		SecondaryMonitor.mod.logFromSecondMC(getMsg());
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(getMsg().length);
		data.write(getMsg());
	}
	
	@Override
	public ConsolePacket template() {
		return new SystemOutputPacket(getId());
	}

	@Override
	public boolean needMainThread() {
		return false;
	}
}
