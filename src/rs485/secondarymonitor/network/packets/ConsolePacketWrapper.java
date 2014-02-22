package rs485.secondarymonitor.network.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import rs485.secondarymonitor.network.abstractpackets.ModernPacket;
import rs485.secondarymonitor.proxy.ClientProxy;
import rs485.secondarymonitor.proxy.MainProxy;

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
		((ClientProxy)MainProxy.proxy).sendConsolePacket(getData());
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
