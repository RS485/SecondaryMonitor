package rs485.secondarymonitor.secondjvm.mapwriter;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import mapwriter.region.BlockColours;
import mapwriter.region.MwChunk;
import mapwriter.region.Region;
import mapwriter.region.RegionManager;

public class SDMSndRegionManager extends RegionManager {
	
	private final LinkedHashMap<Long, Region> regionMap;
	
	public SDMSndRegionManager(File worldDir, File imageDir, BlockColours blockColours, int minZoom, int maxZoom) {
		super(worldDir, imageDir, blockColours, minZoom, maxZoom);
		try {
			Field fMap = RegionManager.class.getDeclaredField("regionMap");
			fMap.setAccessible(true);
			regionMap = (LinkedHashMap<Long, Region>)fMap.get(this);
		} catch(NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch(SecurityException e) {
			throw new RuntimeException(e);
		} catch(IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Region getRegion(int x, int z, int zoomLevel, int dimension) {
		Region region = this.regionMap.get(Region.getKey(x, z, zoomLevel, dimension));
		if (region == null) {
			// add region
			region = new SDMSndRegion(this, x, z, zoomLevel, dimension);
			this.regionMap.put(region.key, region);
		}
		return region;
	}

	public void updateChunk(int regionX, int regionZ, int zoomLevel, int dimension, int x, int z, int[] updateData) {
		SDMSndRegion region = (SDMSndRegion)getRegion(regionX, regionZ, zoomLevel, dimension);
		region.updateChunk(x, z, updateData);
		MapWriterHelper.instance().getMW().mapTexture.updateTextureFromRegion(region, region.x, region.z, region.size, region.size);
	}

	public void updateRegion(int regionX, int regionZ, int zoomLevel, int dimension, int[] updateData) {
		SDMSndRegion region = (SDMSndRegion)getRegion(regionX, regionZ, zoomLevel, dimension);
		region.updateRegion(updateData);
		MapWriterHelper.instance().getMW().mapTexture.updateTextureFromRegion(region, region.x, region.z, region.size, region.size);
	}
}
