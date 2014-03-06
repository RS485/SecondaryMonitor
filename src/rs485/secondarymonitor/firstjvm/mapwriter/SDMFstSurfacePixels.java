package rs485.secondarymonitor.firstjvm.mapwriter;

import java.io.File;

import mapwriter.region.MwChunk;
import mapwriter.region.Region;
import mapwriter.region.SurfacePixels;
import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.map.ChunkUpdatePacket;
import rs485.secondarymonitor.connection.packets.map.RegionUpdatePacket;
import rs485.secondarymonitor.firstjvm.JVMHandler;

public class SDMFstSurfacePixels extends SurfacePixels {
	
	public SDMFstSurfacePixels(Region region, File filename) {
		super(region, filename);
	}
	
	private void chunkChanged(int x, int z, int[] changedPixels) {
		JVMHandler.instance().sendConsolePacket(ConsolePacketHandler.getPacket(ChunkUpdatePacket.class).setRegionX(this.region.x).setRegionZ(this.region.z).setZoomLevel(this.region.zoomLevel).setDimension(this.region.dimension).setX(x).setZ(z).setUpdateData(changedPixels));
	}
	
	private void regionChanged() {
		JVMHandler.instance().sendConsolePacket(ConsolePacketHandler.getPacket(RegionUpdatePacket.class).setRegionX(this.region.x).setRegionZ(this.region.z).setZoomLevel(this.region.zoomLevel).setDimension(this.region.dimension).setUpdateData(pixels));
	}

	@Override
	public void updateChunk(MwChunk chunk) {
		int x = (chunk.x << 4);
		int z = (chunk.z << 4);
		int offset = this.region.getPixelOffset(x, z);
		int scanSize = Region.SIZE;
		int[] oldpixels = null;
		boolean changed = false;
		if(this.pixels != null) {
			oldpixels = new int[MwChunk.SIZE * MwChunk.SIZE];
			for (int lz = 0; lz < MwChunk.SIZE; lz++) {
				for (int lx = 0; lx < MwChunk.SIZE; lx++) {
					int pixelOffset = offset + (lz * scanSize) + lx;
					int originalPos = (lz * MwChunk.SIZE) + lx;
					oldpixels[originalPos] = pixels[pixelOffset];
				}
			}
		} else {
			changed = true;
		}
		super.updateChunk(chunk);
		if(!changed) {
			for (int lz = 0; lz < MwChunk.SIZE; lz++) {
				for (int lx = 0; lx < MwChunk.SIZE; lx++) {
					int pixelOffset = offset + (lz * scanSize) + lx;
					int originalPos = (lz * MwChunk.SIZE) + lx;
					if(pixels[pixelOffset] != oldpixels[originalPos]) {
						changed = true;
						break;
					}
				}
			}
		}
		if(changed) {
			int[] changedpixels = new int[MwChunk.SIZE * MwChunk.SIZE];
			for (int lz = 0; lz < MwChunk.SIZE; lz++) {
				for (int lx = 0; lx < MwChunk.SIZE; lx++) {
					int pixelOffset = offset + (lz * scanSize) + lx;
					int originalPos = (lz * MwChunk.SIZE) + lx;
					changedpixels[originalPos] = pixels[pixelOffset];
				}
			}
			chunkChanged(x, z, changedpixels);
		}
	}

	@Override
	public int[] getPixels() {
		if (this.pixels == null) {
			int[] tmp = super.getPixels();
			if(tmp != null) {
				regionChanged();
			}
			return tmp;
		} else {
			return super.getPixels();
		}
	}
	
}
