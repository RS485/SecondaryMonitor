package rs485.secondarymonitor.firstjvm.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ControllableGuiButton extends GuiButton {

	private IDrawButtonController drawController;
	private IEnableButtonController enableController;
	private IContentButtonController contentController;
	
	public ControllableGuiButton(int par1, int par2, int par3, Object... controllers) {
		super(par1, par2, par3, null);
		loadController(controllers);
	}

	public ControllableGuiButton(int par1, int par2, int par3, int par4, int par5, Object... controllers) {
		super(par1, par2, par3, par4, par5, null);
		loadController(controllers);
	}

	private void loadController(Object[] controllers) {
		if(controllers != null && controllers.length > 0) {
			for(Object object: controllers) {
				if(object instanceof IDrawButtonController) {
					if(drawController != null) throw new RuntimeException("Only one controller per type");
					drawController = (IDrawButtonController) object;
				}
				if(object instanceof IEnableButtonController) {
					if(enableController != null) throw new RuntimeException("Only one controller per type");
					enableController = (IEnableButtonController) object;
				}
				if(object instanceof IContentButtonController) {
					if(contentController != null) throw new RuntimeException("Only one controller per type");
					contentController = (IContentButtonController) object;
				}
				if(object instanceof String) {
					this.displayString = (String) object;
				}
			}
		}
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
		if(drawController != null) this.drawButton = drawController.shouldButtonBeDrawn(this);
		if(enableController != null) this.enabled = enableController.isButtonEnabled(this);
		if(contentController != null) this.displayString = contentController.getContent(this);
		super.drawButton(par1Minecraft, par2, par3);
	}

	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		if(enableController != null) this.enabled = enableController.isButtonEnabled(this);
		return super.mousePressed(par1Minecraft, par2, par3);
	}
}
