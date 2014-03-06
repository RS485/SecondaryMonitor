package rs485.secondarymonitor.firstjvm.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class SDMJVMScreen extends GuiScreen {

	private final GuiScreen parentScreen;
	
	public SDMJVMScreen(GuiScreen parent) {
		parentScreen = parent;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(new ControllableGuiButton(7, this.width / 2 - 100, this.height / 4 + 24 * 6, "Done"));
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "JVM Parameter", this.width / 2, 40, 16777215);
        this.drawCenteredString(this.fontRenderer, "Changes on this page will only apply after a restart of the Second Screen", this.width / 2, this.height / 4 + 24 * 5, 16777215);
		super.drawScreen(par1, par2, par3);
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch(par1GuiButton.id) {
			case 1:
				break;
			case 2:
				break;
			case 7:
				this.mc.displayGuiScreen(parentScreen);
				break;
		}
	}
}
