package rs485.secondarymonitor.secondjvm.mapwriter;

import java.io.File;

import rs485.secondarymonitor.secondjvm.Main;

import mapwriter.BackgroundExecutor;
import mapwriter.ChunkManager;
import mapwriter.Mw;
import mapwriter.MwUtil;
import mapwriter.map.MapTexture;
import mapwriter.map.MarkerManager;
import mapwriter.map.MiniMap;
import mapwriter.map.Trail;
import mapwriter.map.UndergroundTexture;
import mapwriter.region.MwChunk;
import mapwriter.region.RegionManager;

public class MapWriterHelper {
	private static MapWriterHelper instance;
	public static MapWriterHelper instance() {
		if(instance == null) {
			instance = new MapWriterHelper();
		}
		return instance;
	}
	
	//Class Content	
	private Mw mw;
	
	public Mw getMW() {
		if(mw == null) {
			mw = Mw.instance;
		}
		return mw;
	}
	
	public void init() {
		getMW().worldName = getMW().getWorldName();
		
		// get world and image directories
		File saveDir = new File(getMW().mc.mcDataDir, "saves");
		if (getMW().saveDirOverride.length() > 0) {
			File d = new File(getMW().saveDirOverride);
			if (d.isDirectory()) {
				saveDir = d;
			} else {
				MwUtil.log("error: no such directory %s", getMW().saveDirOverride);
			}
		}
		
		getMW().worldDir = new File(new File(saveDir, "mapwriter_sdm_worlds"), getMW().worldName);
		
		// create directories
		getMW().imageDir = new File(getMW().worldDir, "images");
		if (!getMW().imageDir.exists()) {
			getMW().imageDir.mkdirs();
		}
		if (!getMW().imageDir.isDirectory()) {
			MwUtil.log("Mapwriter: ERROR: could not create images directory '%s'", getMW().imageDir.getPath());
		}
		
		getMW().tickCounter = 0;
		//getMW().onPlayerDeathAlreadyFired = false;
		
		getMW().loadConfig();
		//this.multiplayer = !this.mc.isIntegratedServerRunning();
		
		// marker manager only depends on the config being loaded
		getMW().markerManager = new MarkerManager();
		getMW().markerManager.load(getMW().worldConfig, getMW().catMarkers);
		
		getMW().playerTrail = new Trail(getMW(), "player");
		
		// executor does not depend on anything
		getMW().executor = new BackgroundExecutor();
		
		// mapTexture depends on config being loaded
		getMW().mapTexture = new MapTexture(getMW().textureSize, getMW().linearTextureScalingEnabled);
		getMW().undergroundMapTexture = new UndergroundTexture(getMW(), getMW().textureSize, getMW().linearTextureScalingEnabled);
		getMW().reloadBlockColours();
		// region manager depends on config, mapTexture, and block colours
		getMW().regionManager = new SDMSndRegionManager(getMW().worldDir, getMW().imageDir, getMW().blockColours, getMW().minZoom, getMW().maxZoom);
		// overlay manager depends on mapTexture
		getMW().miniMap = new MiniMap(getMW());
		getMW().miniMap.view.setDimension(0);
		
		getMW().chunkManager = new ChunkManager(getMW());
		
		getMW().ready = true;
	}
	
	public void updatePlayer() {
		Main.instance().lockPlayer();
		MapWriterHelper.instance().getMW().playerX = Main.instance().getPlayer().posX;
		MapWriterHelper.instance().getMW().playerY = Main.instance().getPlayer().posY;
		MapWriterHelper.instance().getMW().playerZ = Main.instance().getPlayer().posZ;
		MapWriterHelper.instance().getMW().playerXInt = (int) Math.floor(Main.instance().getPlayer().posX);
		MapWriterHelper.instance().getMW().playerYInt = (int) Math.floor(Main.instance().getPlayer().posY);
		MapWriterHelper.instance().getMW().playerZInt = (int) Math.floor(Main.instance().getPlayer().posZ);
		MapWriterHelper.instance().getMW().playerHeading = Math.toRadians(Main.instance().getPlayer().rotationYaw) + (Math.PI / 2.0D);
		MapWriterHelper.instance().getMW().mapRotationDegrees = -Main.instance().getPlayer().rotationYaw + 180;
		Main.instance().unlockPlayer();
	}
	
	public void updateChunk(int regionX, int regionZ, int zoomLevel, int dimension, int x, int z, int[] updateData) {
		((SDMSndRegionManager)getMW().regionManager).updateChunk(regionX, regionZ, zoomLevel, dimension, x, z, updateData);

	}

	public void updateRegion(int regionX, int regionZ, int zoomLevel, int dimension, int[] updateData) {
		((SDMSndRegionManager)getMW().regionManager).updateRegion(regionX, regionZ, zoomLevel, dimension, updateData);
	}
}
