package rs485.secondarymonitor.secondjvm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.ShutdownPacket;
import rs485.secondarymonitor.proxy.ClientProxy;
import rs485.secondarymonitor.proxy.MainProxy;
import rs485.secondarymonitor.secondjvm.console.InputOutputHelper;
import rs485.secondarymonitor.secondjvm.gui.ChatGui;
import rs485.secondarymonitor.secondjvm.gui.GuiPlayerSkin;
import rs485.secondarymonitor.secondjvm.gui.ISDMGui;
import rs485.secondarymonitor.secondjvm.gui.PlayerInventoryGui;
import rs485.secondarymonitor.secondjvm.gui.RenderHelper;

public class Main {
	
	private Minecraft mc;
	private boolean init;
	private List<ISDMGui> currentGui = new ArrayList<ISDMGui>();
	private static final ResourceLocation MOUSE_CURSOR = new ResourceLocation("secondarymonitor", "textures/pointer.png");
	private int mousePos_X = 20;
	private int mousePos_Y = 20;
	private boolean displayMouse = true;
	
	private EntityPlayerSP player;
	
	//All available GUIs
	public ChatGui chatGui = new ChatGui();
	public GuiPlayerSkin skinGui = new GuiPlayerSkin();
	public PlayerInventoryGui invGui = new PlayerInventoryGui();
	
	public Main(Minecraft mc) {
		this.mc = mc;
	}
	
	public void runGameLoop() {
		((ClientProxy)MainProxy.proxy).packetProcessor.tickEnd(null);
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
       	mc.renderEngine.bindTexture(MOUSE_CURSOR);
        if(displayMouse) {
	       	GL11.glPushMatrix();
	        GL11.glTranslated(mousePos_X, mousePos_Y, 1000);
	        GL11.glScaled(0.02, 0.02, 1);
	    	RenderHelper.drawTexturedModalRect(0, 0, 0, 0, 128 * 2, 128 * 2);
	       	GL11.glPopMatrix();
        }
		RenderHelper.disableStandardItemLighting();

		
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
		/*
		public WorldClient(Main main) {
	    	super(new SaveHandlerMP(), "MpServer", new WorldSettings(0, EnumGameType.CREATIVE, false, false, WorldType.DEFAULT), null, new Profiler(), null);
	    	this.isRemote = true;
	        this.setSpawnLocation(8, 64, 8);
	    }
		*/
		RenderManager.instance.cacheActiveRenderInfo(null, mc.getTextureManager(), mc.fontRenderer, new EntityPlayerSP(mc, mc.theWorld, mc.getSession(), 0), null, mc.gameSettings, 1);
		//RenderManager.instance.itemRenderer = new ItemRenderer(mc);
		mc.playerController = new PlayerControllerMP(mc, null);
		mc.isGamePaused = true;
		mc.displayInGameMenu();
		InputOutputHelper.instance().gameStarted();
		currentGui.add(chatGui);
		currentGui.add(skinGui);
		currentGui.add(invGui);
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		mousePos_X = scaledresolution.getScaledWidth() / 2;
		mousePos_Y = scaledresolution.getScaledHeight() / 2;
	}

	public void updateMousePosition(int deltaX, int deltaY) {
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		mousePos_X += deltaX * 0.5;
		mousePos_Y -= deltaY * 0.5;
		mousePos_X = Math.min(scaledresolution.getScaledWidth(), Math.max(0, mousePos_X));
		mousePos_Y = Math.min(scaledresolution.getScaledHeight(), Math.max(0, mousePos_Y));
	}
	
	public void setMouseDisplay(boolean flag) {
		displayMouse = flag;
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
