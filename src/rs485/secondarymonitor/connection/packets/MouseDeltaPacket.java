package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.secondjvm.Main;

@Accessors(chain=true)
public class MouseDeltaPacket extends ConsolePacket {

	public MouseDeltaPacket(int id) {
		super(id);
	}

	@Getter
	@Setter
	private int mouseX;

	@Getter
	@Setter
	private int mouseY;
	
	@Override
	public boolean needMainThread() {
		return true;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		setMouseX(data.readInt());
		setMouseY(data.readInt());
	}
	
	@Override
	public void processPacket() {
		Main.instance(null).updateMousePosition(getMouseX(), getMouseY());
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(getMouseX());
		data.writeInt(getMouseY());
	}
	
	@Override
	public ConsolePacket template() {
		return new MouseDeltaPacket(getId());
	}
}
