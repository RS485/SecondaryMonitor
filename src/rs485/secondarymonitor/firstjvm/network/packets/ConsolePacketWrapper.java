package rs485.secondarymonitor.firstjvm.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import rs485.secondarymonitor.firstjvm.JVMHandler;
import rs485.secondarymonitor.firstjvm.network.abstractpackets.ModernPacket;

@Accessors(chain=true)
public class ConsolePacketWrapper extends ModernPacket {
	
	@Getter
	@Setter
	private byte[] data;
	
	public ConsolePacketWrapper(int id) {
		super(id);
	}

	@Override
	public void readData(DataInputStream dataStream) throws IOException {
		data = new byte[dataStream.readInt()];
		dataStream.read(data);
	}
	
	@Override
	public void processPacket(EntityPlayer player) {
		if(JVMHandler.instance().isRunning() && JVMHandler.instance().isJVMCompleted()) {
			JVMHandler.instance().sendConsolePacket(getData());
		}
	}
	
	@Override
	public void writeData(DataOutputStream dataStream) throws IOException {
		dataStream.writeInt(data.length);
		dataStream.write(data);
	}
	
	@Override
	public ModernPacket template() {
		return new ConsolePacketWrapper(getId());
	}
}
