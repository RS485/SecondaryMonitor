package rs485.secondarymonitor.secondjvm.mapwriter;

import java.io.File;

import mapwriter.region.MwChunk;
import mapwriter.region.Region;
import mapwriter.region.SurfacePixels;

public class SDMSndSurfacePixels extends SurfacePixels {

	public SDMSndSurfacePixels(Region region, File filename) {
		super(region, filename);
	}

	public void updateChunk(int x, int z, int[] updateData) {
		int[] pixels = this.getOrAllocatePixels();
		for (int lz = 0; lz < MwChunk.SIZE; lz++) {
			for (int lx = 0; lx < MwChunk.SIZE; lx++) {
				int offset = this.region.getPixelOffset(x, z);
				int scanSize = Region.SIZE;
				int pixelOffset = offset + (lz * scanSize) + lx;
				int originalPos = (lz * MwChunk.SIZE) + lx;
				pixels[pixelOffset] = updateData[originalPos];
			}
		}
	}

	public void updateRegion(int[] updateData) {
		this.pixels = updateData;
	}
}
