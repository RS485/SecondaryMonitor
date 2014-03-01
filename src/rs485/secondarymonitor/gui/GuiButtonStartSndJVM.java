package rs485.secondarymonitor.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiButtonStartSndJVM extends GuiButton {
	protected static final ResourceLocation buttonTextures = new ResourceLocation("secondarymonitor", "textures/widgets.png");
	public GuiButtonStartSndJVM(int par1, int par2, int par3) {
		super(par1, par2, par3, 20, 20, "");
	}
	
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		//TODO Don't draw the button when the second jvm is running
		if(this.drawButton) {
			par1Minecraft.getTextureManager().bindTexture(buttonTextures);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean flag = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
			int k = 0;
			
			if(flag) {
				k += this.height;
			}
			
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, k, this.width, this.height);
		}
	}
}
