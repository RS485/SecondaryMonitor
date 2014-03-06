package rs485.secondarymonitor.firstjvm.renderer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.FMLClientHandler;

public class MinecraftInfoRenderer {
	
	private static final int	BYTES_PER_PIXEL	= 4;
	private static int			FBO;
	private static int			Tex;
	private static int			Depth;
	
	private final int			heigth, width;
	private Minecraft			mc;
	private final ByteBuffer	data;
	private final BufferedImage	bufImage;
	private int					oldHeigth, oldWidth;
	
	public MinecraftInfoRenderer(int width, int heigth) {
		this.heigth = heigth;
		this.width = width;
		bufImage = new BufferedImage(width, heigth, BufferedImage.TYPE_4BYTE_ABGR);
		data = BufferUtils.createByteBuffer(width * heigth * BYTES_PER_PIXEL);
		mc = FMLClientHandler.instance().getClient();
	}
	
	public void startRender() {
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, FBO);
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, Tex, 0);
		ARBFramebufferObject.glFramebufferTexture2D(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, ARBFramebufferObject.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, Depth, 0);
		oldWidth = mc.displayWidth;
		oldHeigth = mc.displayHeight;
		mc.displayWidth = width;
		mc.displayHeight = heigth;
	}
	
	public void createResult() {
		GL11.glReadPixels(0, 0, width, heigth, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)data.clear());
		Raster r = bufImage.getRaster();
		DataBuffer buffer = r.getDataBuffer();
		try {
			Field byteData = DataBufferByte.class.getDeclaredField("data");
			byteData.setAccessible(true);
			byte[] testData = (byte[])byteData.get(buffer);
			int length = testData.length;
			byte[] tmp = new byte[length];
			data.get(tmp);
			for(int i = 0; i < length; i += 4) {
				testData[i + 0] = tmp[length - i - 1];
				testData[i + 1] = tmp[length - i - 2];
				testData[i + 2] = tmp[length - i - 3];
				testData[i + 3] = tmp[length - i - 4];
			}
		} catch(NoSuchFieldException e) {
			e.printStackTrace();
		} catch(SecurityException e) {
			e.printStackTrace();
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		}
		ARBFramebufferObject.glBindFramebuffer(ARBFramebufferObject.GL_DRAW_FRAMEBUFFER, 0);
		mc.displayWidth = oldWidth;
		mc.displayHeight = oldHeigth;
	}
	
	public BufferedImage getResult() {
		return bufImage;
	}

	public static void init() {
		FBO = ARBFramebufferObject.glGenFramebuffers();
		Tex = GL11.glGenTextures();
		Depth = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Tex);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB8, 320, 180, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.IntBuffer)null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Depth);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 320, 180, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_INT, (java.nio.IntBuffer)null);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
