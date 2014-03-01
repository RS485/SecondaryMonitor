package rs485.secondarymonitor.asm;

import java.io.IOException;
import java.net.UnknownHostException;

import net.minecraft.client.gui.GuiButton;
import rs485.secondarymonitor.proxy.ClientProxy;
import rs485.secondarymonitor.proxy.MainProxy;

public class ASMHookClass {
	
	/**
	 * @param button
	 * @return true if this method handled the button
	 */
	public static boolean handleMainMenuButton(GuiButton button) {
		if(button.id == 1201) {
			try {
				((ClientProxy)MainProxy.proxy).startSecondJVM();
			} catch(UnknownHostException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
}
