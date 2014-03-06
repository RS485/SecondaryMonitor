package rs485.secondarymonitor.secondjvm.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ReportedException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderHelper {
	public static void drawRect(int par0, int par1, int par2, int par3, int par4) {
		drawRect(par0, par1, par2, par3, par4, 0D);
	}
	
	public static void drawRect(int par0, int par1, int par2, int par3, int par4, double z) {
		int j1;
		
		if(par0 < par2) {
			j1 = par0;
			par0 = par2;
			par2 = j1;
		}
		
		if(par1 < par3) {
			j1 = par1;
			par1 = par3;
			par3 = j1;
		}
		
		float f = (float)(par4 >> 24 & 255) / 255.0F;
		float f1 = (float)(par4 >> 16 & 255) / 255.0F;
		float f2 = (float)(par4 >> 8 & 255) / 255.0F;
		float f3 = (float)(par4 & 255) / 255.0F;
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(f1, f2, f3, f);
		tessellator.startDrawingQuads();
		tessellator.addVertex((double)par0, (double)par3, z);
		tessellator.addVertex((double)par2, (double)par3, z);
		tessellator.addVertex((double)par2, (double)par1, z);
		tessellator.addVertex((double)par0, (double)par1, z);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void renderEntity(int x, int y, int scale, float xRotate, float yRotate, EntityLivingBase entity) {
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, 50.0F);
		GL11.glScalef((float)(-scale), (float)scale, (float)scale);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

		float prevYawOffset 	= entity.renderYawOffset;
		float prevYaw 			= entity.rotationYaw;
		float prevPitch 		= entity.rotationPitch;
		float prevPrevYawHead 	= entity.prevRotationYawHead;
		float prevYawHead 		= entity.rotationYawHead;

		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-((float)Math.atan((double)(yRotate / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

		entity.renderYawOffset 		= (float)Math.atan((double)(xRotate / 40.0F)) * 20.0F;
		entity.rotationYaw 			= (float)Math.atan((double)(xRotate / 40.0F)) * 40.0F;
		entity.rotationPitch 		= -((float)Math.atan((double)(yRotate / 40.0F))) * 20.0F;
		entity.rotationYawHead 		= entity.rotationYaw;
		entity.prevRotationYawHead 	= entity.rotationYaw;

		GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

		entity.renderYawOffset 	= prevYawOffset;
		entity.rotationYaw 		= prevYaw;
		entity.rotationPitch 		= prevPitch;
		entity.prevRotationYawHead = prevPrevYawHead;
		entity.rotationYawHead 	= prevYawHead;

		GL11.glPopMatrix();
		disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	public static void renderEntityWithPosYaw(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
		Render render = null;
		try {
			render = RenderManager.instance.getEntityRenderObject(par1Entity);
			if(render != null && RenderManager.instance.renderEngine != null) {
				try {
					render.doRender(par1Entity, par2, par4, par6, par8, par9);
				} catch(Throwable throwable1) {
					throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
				}
			}
		} catch(Throwable throwable3) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
			par1Entity.addEntityCrashInfo(crashreportcategory);
			CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
			crashreportcategory1.addCrashSection("Assigned renderer", render);
			crashreportcategory1.addCrashSection("Location", CrashReportCategory.func_85074_a(par2, par4, par6));
			crashreportcategory1.addCrashSection("Rotation", Float.valueOf(par8));
			crashreportcategory1.addCrashSection("Delta", Float.valueOf(par9));
			throw new ReportedException(crashreport);
		}
	}
	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
	 */
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		int zLevel = 0;
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)zLevel, (double)((float)(u + 0) * f), (double)((float)(v + height) * f1));
		tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)zLevel, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
		tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)zLevel, (double)((float)(u + width) * f), (double)((float)(v + 0) * f1));
		tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)zLevel, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
		tessellator.draw();
	}

	public static void enableStandardItemLighting() {
		net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
	}

	public static void disableStandardItemLighting() {
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
	}
	
	public static void enableGUIStandardItemLighting() {
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
	}
}
