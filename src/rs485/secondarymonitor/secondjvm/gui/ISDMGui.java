package rs485.secondarymonitor.secondjvm.gui;

import net.minecraft.client.Minecraft;

public interface ISDMGui {
	public void renderGui(Minecraft mc);
	public void handleMouseOverAt(int x, int y);
	public void handleMouseClickAt(int x, int y);
}
