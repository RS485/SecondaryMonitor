package rs485.secondarymonitor.firstjvm.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SDMSettingsScreen extends GuiScreen {
	
	private final GuiScreen parentScreen;
	
	public SDMSettingsScreen(GuiScreen parent) {
		parentScreen = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new ControllableGuiButton(1, this.width / 2 - 100, this.height / 4 + 24 * 0, "JVM Parameter"));
		this.buttonList.add(new ControllableGuiButton(2, this.width / 2 - 100, this.height / 4 + 24 * 1, "Feature Controller"));
		this.buttonList.add(new ControllableGuiButton(3, this.width / 2 - 100, this.height / 4 + 24 * 2, "Mod Controller"));
		this.buttonList.add(new ControllableGuiButton(7, this.width / 2 - 100, this.height / 4 + 24 * 6, "Done"));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "SDM Settings", this.width / 2, 40, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch(par1GuiButton.id) {
			case 1:
				this.mc.displayGuiScreen(new SDMJVMScreen(this));
				break;
			case 7:
				this.mc.displayGuiScreen(parentScreen);
				break;
		}
	}
}
