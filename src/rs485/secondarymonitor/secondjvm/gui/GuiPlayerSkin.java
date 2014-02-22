package rs485.secondarymonitor.secondjvm.gui;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import rs485.secondarymonitor.secondjvm.Main;

public class GuiPlayerSkin implements ISDMGui {

	@Override
	public void renderGui(Minecraft mc) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(Main.instance().getPlayer() != null) {
			Main.instance().getPlayer().updateEntityActionState();
			Main.instance().getPlayer().onLivingUpdate();
			Main.instance().getPlayer().movementInput.moveForward = 0F;
			RenderHelper.renderEntity(120, 140, 40, -100, 2, Main.instance().getPlayer());
		}
	}
	
	@Override
	public void handleMouseOverAt(int x, int y) {}
	
	@Override
	public void handleMouseClickAt(int x, int y) {}
}
