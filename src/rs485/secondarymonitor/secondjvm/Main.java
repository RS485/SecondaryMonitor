package rs485.secondarymonitor.secondjvm;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.LoadingScreenRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumOS;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Util;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.ShutdownPacket;
import rs485.secondarymonitor.proxy.MainProxy;
import rs485.secondarymonitor.secondjvm.gui.ChatGui;
import rs485.secondarymonitor.secondjvm.gui.GuiPlayerSkin;
import rs485.secondarymonitor.secondjvm.gui.ISDMGui;
import rs485.secondarymonitor.secondjvm.gui.PlayerInventoryGui;
import rs485.secondarymonitor.secondjvm.gui.RenderHelper;
import rs485.secondarymonitor.secondjvm.mapwriter.MapWriterHelper;
import cpw.mods.fml.client.FMLClientHandler;

public class Main {
	
	private Minecraft mc;
	private boolean init;
	private List<ISDMGui> currentGui = new ArrayList<ISDMGui>();
	
	private EntityPlayerSP player;
	protected final ReentrantReadWriteLock playerLock = new ReentrantReadWriteLock();
	public final Lock playerreadLock = playerLock.readLock();
	public final Lock playerwriteLock = playerLock.writeLock();
	
	//All available GUIs
	public ChatGui chatGui = new ChatGui();
	public GuiPlayerSkin skinGui = new GuiPlayerSkin();
	public PlayerInventoryGui invGui = new PlayerInventoryGui();
	
	public Main(Minecraft mc) {
		this.mc = mc;
	}
	
    public void startGame() throws LWJGLException {
		System.out.println("Start Game 1");
		mc.gameSettings = new GameSettings(mc, new File("ThisIsAnNotExistingDirectory-74389205623546239")); // TODO: Sync Settings
		
		if(mc.gameSettings.overrideHeight > 0 && mc.gameSettings.overrideWidth > 0) {
			mc.displayWidth = mc.gameSettings.overrideWidth;
			mc.displayHeight = mc.gameSettings.overrideHeight;
		}
		
		if(mc.isFullScreen()) {
			Display.setFullscreen(true);
			mc.displayWidth = Display.getDisplayMode().getWidth();
			mc.displayHeight = Display.getDisplayMode().getHeight();
			
			if(mc.displayWidth <= 0) {
				mc.displayWidth = 1;
			}
			
			if(mc.displayHeight <= 0) {
				mc.displayHeight = 1;
			}
		} else {
			Display.setDisplayMode(new DisplayMode(mc.displayWidth, mc.displayHeight));
		}
		
		Display.setResizable(true);
		Display.setTitle("Secondary Monitor Minecraft 1.6.4");
		mc.getLogAgent().logInfo("LWJGL Version: " + Sys.getVersion());
		
		if(Util.getOSType() != EnumOS.MACOS) {
			try {
				System.out.println(new File(mc.mcDataDir, "/icon.png").getAbsolutePath());
				Display.setIcon(new ByteBuffer[] { mc.readImage(new File(mc.mcDataDir, "/icon.png")) });
			} catch(IOException ioexception) {
				ioexception.printStackTrace();
			}
		}
		
		try {
			ForgeHooksClient.createDisplay();
		} catch(LWJGLException lwjglexception) {
			lwjglexception.printStackTrace();
			
			try {
				Thread.sleep(1000L);
			} catch(InterruptedException interruptedexception) {
				;
			}
			
			if(mc.isFullScreen()) {
				mc.updateDisplayMode();
			}
			
			Display.create();
		}
		
		OpenGlHelper.initializeTextures();
		mc.guiAchievement = new GuiAchievement(mc);
		mc.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
		mc.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
		mc.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
		mc.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
		mc.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
		mc.saveLoader = new AnvilSaveConverter(new File(mc.mcDataDir, "saves"));
		String loc = mc.fileResourcepacks.getAbsolutePath();
		String[] s = loc.split("SDM_mcDir");
		loc = s[0] + "." + s[1];
		mc.mcResourcePackRepository = new ResourcePackRepository(new File(loc), mc.mcDefaultResourcePack, mc.metadataSerializer_, mc.gameSettings);
		mc.mcResourceManager = new SimpleReloadableResourceManager(mc.metadataSerializer_);
		mc.mcLanguageManager = new LanguageManager(mc.metadataSerializer_, mc.gameSettings.language);
		mc.mcResourceManager.registerReloadListener(mc.mcLanguageManager);
		mc.refreshResources();
		mc.renderEngine = new TextureManager(mc.mcResourceManager);
		mc.mcResourceManager.registerReloadListener(mc.renderEngine);
		mc.sndManager = new SoundManager(mc.mcResourceManager, mc.gameSettings, mc.fileAssets);
		mc.sndManager.LOAD_SOUND_SYSTEM = false;
		mc.mcResourceManager.registerReloadListener(mc.sndManager);
		mc.loadScreen();
		mc.fontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
		
		FMLClientHandler.instance().beginMinecraftLoading(mc, mc.defaultResourcePacks, mc.mcResourceManager);
		
		if(mc.gameSettings.language != null) {
			mc.fontRenderer.setUnicodeFlag(mc.mcLanguageManager.isCurrentLocaleUnicode());
			mc.fontRenderer.setBidiFlag(mc.mcLanguageManager.isCurrentLanguageBidirectional());
		}
		
		mc.standardGalacticFontRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), mc.renderEngine, false);
		mc.mcResourceManager.registerReloadListener(mc.fontRenderer);
		mc.mcResourceManager.registerReloadListener(mc.standardGalacticFontRenderer);
		mc.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		mc.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		RenderManager.instance.itemRenderer = new ItemRenderer(mc);
		mc.entityRenderer = new EntityRenderer(mc);
		mc.statFileWriter = new StatFileWriter(mc.getSession(), mc.mcDataDir);
		// AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(mc));
		mc.mouseHelper = new MouseHelper();
		checkGLError("Pre startup");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1.0D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		checkGLError("Startup");
		mc.renderGlobal = new RenderGlobal(mc);
		mc.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, new TextureMap(0, "textures/blocks"));
		mc.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		mc.effectRenderer = new EffectRenderer(mc.theWorld, mc.renderEngine);
		FMLClientHandler.instance().finishMinecraftLoading();
		checkGLError("Post startup");
		mc.ingameGUI = new GuiIngameForge(mc);
		
		mc.displayGuiScreen(new GuiMainMenu());
		
		mc.loadingScreen = new LoadingScreenRenderer(mc);
		
		if(mc.gameSettings.fullScreen && !mc.isFullScreen()) {
			mc.toggleFullscreen();
		}
		FMLClientHandler.instance().onInitializationComplete();
	}
	
	public void runGameLoop() {
		MainProxy.packetProcessor.tickEnd(null);
		if(!init) {
			init();
			init = true;
		}
		
		AxisAlignedBB.getAABBPool().cleanPool();
		
		if(mc.theWorld != null) {
			mc.theWorld.getWorldVec3Pool().clear();
		}
		
		
		if(Display.isCloseRequested()) {
			InputOutputHelper.instance().sendToMain(ConsolePacketHandler.getPacket(ShutdownPacket.class));
			mc.shutdown();
		}
		
		mc.timer.updateTimer();
		
		long i = System.nanoTime();
		
		//TODO Handle Gui Input
		/*
		for(int j = 0; j < mc.timer.elapsedTicks; ++j) {
			 mc.runTick();
		}
		*/
		
		long k = System.nanoTime() - i;
		checkGLError("Pre render");
		RenderBlocks.fancyGrass = mc.gameSettings.fancyGraphics;
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		if(!Keyboard.isKeyDown(65)) {
			Display.update();
		}
		
		
		//if(!mc.skipRenderWorld) {
			//FMLCommonHandler.instance().onRenderTickStart(mc.timer.renderPartialTicks);
			//mc.entityRenderer.updateCameraAndRender(mc.timer.renderPartialTicks);
			//FMLCommonHandler.instance().onRenderTickEnd(mc.timer.renderPartialTicks);
		//}
		
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        mc.entityRenderer.setupOverlayRendering();
        
        
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		RenderHelper.enableStandardItemLighting();
        GL11.glPushMatrix();
        RenderHelper.drawRect(0, 0, mc.displayHeight, mc.displayWidth, 0xff000000);
       	GL11.glPopMatrix();
       	for(ISDMGui gui: currentGui) {
       		GL11.glPushMatrix();
            gui.renderGui(mc);
           	GL11.glPopMatrix();
        }
		RenderHelper.disableStandardItemLighting();

		/*
		 * TODO:
		 * Temporary MapWriter Tests
		 */
		if(MapWriterHelper.instance().getMW().miniMap != null) {
			MapWriterHelper.instance().updatePlayer();
			MapWriterHelper.instance().getMW().miniMap.view.setViewCentreScaled(MapWriterHelper.instance().getMW().playerX, MapWriterHelper.instance().getMW().playerZ, 0);
			MapWriterHelper.instance().getMW().miniMap.drawCurrentMap();
			int maxTasks = 50;
			while (!MapWriterHelper.instance().getMW().executor.processTaskQueue() && (maxTasks > 0)) {
				maxTasks--;
			}
			MapWriterHelper.instance().getMW().mapTexture.processTextureUpdates();
		} else {
			MapWriterHelper.instance().init();
		}
		
		
		
		GL11.glFlush();
		//TODO handle Fullscreen
		/*
		if(!Display.isActive() && mc.isFullScreen()) {
			mc.toggleFullscreen();
		}
		*/
		
		Thread.yield();
		
		if(Keyboard.isKeyDown(65)) {
			Display.update();
		}
		
		//TODO allow screenshots
		// mc.screenshotListener();
		
		if(!mc.isFullScreen() && Display.wasResized()) {
			mc.displayWidth = Display.getWidth();
			mc.displayHeight = Display.getHeight();
			
			if(mc.displayWidth <= 0) {
				mc.displayWidth = 1;
			}
			
			if(mc.displayHeight <= 0) {
				mc.displayHeight = 1;
			}
			
			resize(mc.displayWidth, mc.displayHeight);
		}
		
		checkGLError("Post render");
		++mc.fpsCounter;
		
		while(Minecraft.getSystemTime() >= mc.debugUpdateTime + 1000L) {
			mc.debugFPS = mc.fpsCounter;
			mc.debug = mc.debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
			WorldRenderer.chunksUpdated = 0;
			mc.debugUpdateTime += 1000L;
			mc.fpsCounter = 0;
		}
		
		
		mc.mcProfiler.endSection();
		
		
		if(getLimitFramerate() > 0) {
			Display.sync(EntityRenderer.performanceToFps(getLimitFramerate()));
		}
	}

	private void checkGLError(String par1Str) {
		int i = GL11.glGetError();
		
		if(i != 0) {
			String s1 = GLU.gluErrorString(i);
			mc.getLogAgent().logSevere("########## GL ERROR ##########");
			mc.getLogAgent().logSevere("@ " + par1Str);
			mc.getLogAgent().logSevere(i + ": " + s1);
		}
	}
	
	private void resize(int par1, int par2) {
		mc.displayWidth = par1 <= 0 ? 1 : par1;
		mc.displayHeight = par2 <= 0 ? 1 : par2;
		if(mc.currentScreen != null) {
			ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, par1, par2);
			int k = scaledresolution.getScaledWidth();
			int l = scaledresolution.getScaledHeight();
			mc.currentScreen.setWorldAndResolution(mc, k, l);
		}
	}
	
	private int getLimitFramerate() {
        return mc.currentScreen != null && mc.currentScreen instanceof GuiMainMenu ? 2 : mc.gameSettings.limitFramerate;
    }
	
	private void init() {
		mc.theWorld = new WorldClient(this);
		RenderManager.instance.cacheActiveRenderInfo(null, mc.getTextureManager(), mc.fontRenderer, new EntityPlayerSP(mc, mc.theWorld, mc.getSession(), 0), null, mc.gameSettings, 1);
		//RenderManager.instance.itemRenderer = new ItemRenderer(mc);
		mc.playerController = new PlayerControllerMP(mc, null);
		mc.isGamePaused = true;
		mc.displayInGameMenu();
		InputOutputHelper.instance().gameStarted();
		currentGui.add(chatGui);
		currentGui.add(skinGui);
		currentGui.add(invGui);
	}

	public void lockPlayer() {
		playerreadLock.lock();
	}

	public EntityPlayerSP getPlayer() {
		if(player == null) {
			String nameString = mc.getSession().getUsername();
			nameString = nameString.substring(0, nameString.length() - 4);
			player = new EntityPlayerSP(mc, mc.theWorld, new Session(nameString, ""), 0);
			player.movementInput = new MovementInput();
			player.movementInput.moveForward = 0.5F;
		}
		return player;
	}
	
	public void unlockPlayer() {
		playerreadLock.unlock();
	}

	public static Main instance(Minecraft mc) {
		if(instance == null) {
			instance = new Main(mc);
		}
		return instance;
	}
	
	public static Main instance() {
		if(instance == null) {
			throw new NullPointerException();
		}
		return instance;
	}
	
	private static Main	instance;
}
