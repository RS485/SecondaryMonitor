package rs485.secondarymonitor.secondjvm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rs485.secondarymonitor.secondjvm.Main;

public class PlayerInventoryGui implements ISDMGui {

	private final ResourceLocation INVENTORY_GUI = new ResourceLocation("secondarymonitor", "textures/inventory.png");
	private final RenderItem itemRenderer = new RenderItem();
	
	@Override
	public void renderGui(Minecraft mc) {
		mc.renderEngine.bindTexture(INVENTORY_GUI);
		RenderHelper.drawTexturedModalRect(150, 50, 0, 0, 200, 89);
		RenderHelper.enableGUIStandardItemLighting();
		for(int i=0;i<4;i++) {
			ItemStack stack = Main.instance().getPlayer().inventory.armorInventory[i];
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 157, 112 - i*18);
			if(stack != null) itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 157, 112 - i*18, stack.stackSize > 1 ? Integer.toString(stack.stackSize) : "");
		}
		for(int x=0;x<9;x++) {
			ItemStack stack = Main.instance().getPlayer().inventory.mainInventory[x];
			itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 182 + x*18, 116);
			if(stack != null) itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 182 + x*18, 116, stack.stackSize > 1 ? Integer.toString(stack.stackSize) : "");
			for(int y=1;y<4;y++) {
				stack = Main.instance().getPlayer().inventory.mainInventory[(y * 9) + x];
				itemRenderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 182 + x*18, 58 - 18 + y*18);
				if(stack != null) itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, stack, 182 + x*18, 58 - 18 + y*18, stack.stackSize > 1 ? Integer.toString(stack.stackSize) : "");
			}
		}
	}
	
	@Override
	public void handleMouseOverAt(int x, int y) {}
	
	@Override
	public void handleMouseClickAt(int x, int y) {}
}
