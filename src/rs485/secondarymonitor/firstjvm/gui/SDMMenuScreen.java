package rs485.secondarymonitor.firstjvm.gui;

import java.io.IOException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import rs485.secondarymonitor.firstjvm.JVMHandler;

public class SDMMenuScreen extends GuiScreen {
	
	private final GuiScreen parentScreen;
	
	public SDMMenuScreen(GuiScreen parent) {
		parentScreen = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		
		this.buttonList.clear();
		this.buttonList.add(new ControllableGuiButton(1, this.width / 2 - 100, this.height / 4, "Start Second Screen", 
			new IEnableButtonController() {
				@Override
					public boolean isButtonEnabled(GuiButton button) {
						return !JVMHandler.instance().isStarting() && !JVMHandler.instance().isRunning() && Minecraft.getMinecraft().theWorld == null;
					}
				},
			new IContentButtonController() {
				@Override
				public String getContent(GuiButton button) {
					if(JVMHandler.instance().isStarting()) {
						return "Second Screen is starting...";
					} else if(JVMHandler.instance().isRunning()) { //TODO maybe create stop button
						return "Second Screen is running";
					} else if (Minecraft.getMinecraft().theWorld != null) {
						return "Can't start with loaded World";
					}
					return "Start Second Screen";
				}
			}
			));
		this.buttonList.add(new ControllableGuiButton(2, this.width / 2 - 100, this.height / 4 + 24 * 1, "Configuration"));
		this.buttonList.add(new ControllableGuiButton(7, this.width / 2 - 100, this.height / 4 + 24 * 6, "Done"));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "SDM menu", this.width / 2, 40, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch(par1GuiButton.id) {
			case 1:
				try {
					JVMHandler.instance().startSecondJVM();
				} catch(UnknownHostException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				this.mc.displayGuiScreen(new SDMSettingsScreen(this));
				break;
			case 7:
				this.mc.displayGuiScreen(parentScreen);
				break;
		}
	}
}
