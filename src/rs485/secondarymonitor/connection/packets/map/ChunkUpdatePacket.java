package rs485.secondarymonitor.connection.packets.map;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.secondjvm.mapwriter.MapWriterHelper;

@Accessors(chain=true)
public class ChunkUpdatePacket extends ConsolePacket {
	
	@Setter
	@Getter
	private int regionX;
	
	@Setter
	@Getter
	private int regionZ;
	
	@Setter
	@Getter
	private int zoomLevel;
	
	@Setter
	@Getter
	private int dimension;
	
	@Setter
	@Getter
	private int x;
	
	@Setter
	@Getter
	private int z;

	@Setter
	private int[] updateData;
	
	public ChunkUpdatePacket(int id) {
		super(id);
	}

	@Override
	public boolean needMainThread() {
		return false;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		regionX = data.readInt();
		regionZ = data.readInt();
		zoomLevel = data.readInt();
		dimension = data.readInt();
		x = data.readInt();
		z = data.readInt();
		updateData = new int[data.readInt()];
		for(int i=0;i<updateData.length;i++) {
			updateData[i] = data.readInt();
		}
	}
	
	@Override
	public void processPacket() {
		MapWriterHelper.instance().updateChunk(regionX, regionZ, zoomLevel, dimension, getX(), getZ(), updateData);
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(getRegionX());
		data.writeInt(getRegionZ());
		data.writeInt(getZoomLevel());
		data.writeInt(getDimension());
		data.writeInt(getX());
		data.writeInt(getZ());
		data.writeInt(updateData.length);
		for(int i=0;i<updateData.length;i++) {
			data.writeInt(updateData[i]);
		}
	}
	
	@Override
	public ConsolePacket template() {
		return new ChunkUpdatePacket(getId());
	}
	
}
