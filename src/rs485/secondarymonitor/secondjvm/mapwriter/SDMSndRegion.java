package rs485.secondarymonitor.secondjvm.mapwriter;

import java.io.File;

import mapwriter.region.Region;
import mapwriter.region.RegionManager;

public class SDMSndRegion extends Region {

	public SDMSndRegion(RegionManager regionManager, int x, int z, int zoomLevel, int dimension) {
		super(regionManager, x, z, zoomLevel, dimension);
		File surfaceImageFile = this.getImageFile();
		this.surfacePixels = new SDMSndSurfacePixels(this, surfaceImageFile);
	}

	public void updateChunk(int x, int z, int[] updateData) {
		((SDMSndSurfacePixels)this.surfacePixels).updateChunk(x, z, updateData);
	}

	public void updateRegion(int[] updateData) {
		((SDMSndSurfacePixels)this.surfacePixels).updateRegion(updateData);
	}
}
