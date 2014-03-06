package rs485.secondarymonitor.firstjvm.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import rs485.secondarymonitor.firstjvm.gui.SDMMenuScreen;

public class ASMHookClass {

	/**
	 * @param button
	 * @return true if this method handled the button
	 */
	public static boolean handleMainMenuButton(GuiButton button) {
		if(button.id == 1201) {
			Minecraft.getMinecraft().displayGuiScreen(new SDMMenuScreen(Minecraft.getMinecraft().currentScreen));
			return true;
		}
		return false;
	}

	/**
	 * @param button
	 * @return true if this method handled the button
	 */
	public static boolean handleIngameMenuButton(GuiButton button) {
		if(button.id == 1201) {
			Minecraft.getMinecraft().displayGuiScreen(new SDMMenuScreen(Minecraft.getMinecraft().currentScreen));
			return true;
		}
		return false;
	}
}
