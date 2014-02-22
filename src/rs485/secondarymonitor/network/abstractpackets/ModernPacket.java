package rs485.secondarymonitor.network.abstractpackets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import rs485.secondarymonitor.SecondaryMonitor;

@Accessors(chain=true)
public abstract class ModernPacket {

	@Getter
	private final int id;

	@Getter
	private byte[] data = null;

	public ModernPacket(int id) {
		this.id = id;
	}
	
	public Packet250CustomPayload getPacket() {
		if(data == null) throw new RuntimeException("The packet needs to be created() first;");
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = SecondaryMonitor.SECONDARY_MONITOR_CHANNEL_NAME;
		packet.data = this.data;
		packet.length = packet.data.length;
		return packet;
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
	
	public abstract void readData(DataInputStream data) throws IOException;
	public abstract void processPacket(EntityPlayer player);
	public abstract void writeData(DataOutputStream data) throws IOException;
	public abstract ModernPacket template();
}
