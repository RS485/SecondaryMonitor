package rs485.secondarymonitor.secondjvm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Session;

import org.lwjgl.opengl.GL11;

public class GuiPlayerSkin implements ISDMGui {
	
	private EntityPlayerSP player;
	//protected static final ResourceLocation field_110408_a = new ResourceLocation("textures/gui/container/inventory.png");

	@Override
	public void renderGui(Minecraft mc) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(this.player == null) {
			String nameString = mc.getSession().getUsername();
			nameString = nameString.substring(0, nameString.length() - 4);
			System.out.println(nameString);
			this.player = new EntityPlayerSP(mc, mc.theWorld, new Session(nameString, ""), 0);
			this.player.movementInput = new MovementInput();
			this.player.movementInput.moveForward = 0.5F;
		}
		if(this.player != null) {
			this.player.updateEntityActionState();
			this.player.onLivingUpdate();
			this.player.movementInput.moveForward = 0F;
			RenderHelper.renderEntity(50, 100, 30, -100, 2, this.player);
		}
	}
	
	@Override
	public void handleMouseOverAt(int x, int y) {}
	
	@Override
	public void handleMouseClickAt(int x, int y) {}
}
