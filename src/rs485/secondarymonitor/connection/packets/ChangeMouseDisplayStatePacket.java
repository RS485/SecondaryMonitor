package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.MouseHelper;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;

@Accessors(chain=true)
public class ChangeMouseDisplayStatePacket extends ConsolePacket {
	
	public ChangeMouseDisplayStatePacket(int id) {
		super(id);
	}

	@Getter
	@Setter
	private boolean displayMouse;
	
	@Override
	public boolean needMainThread() {
		return true;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		setDisplayMouse(data.readBoolean());
	}
	
	@Override
	public void processPacket() {
		if(isDisplayMouse()) {
			new MouseHelper().ungrabMouseCursor();
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeBoolean(isDisplayMouse());
	}
	
	@Override
	public ConsolePacket template() {
		return new ChangeMouseDisplayStatePacket(getId());
	}
}
