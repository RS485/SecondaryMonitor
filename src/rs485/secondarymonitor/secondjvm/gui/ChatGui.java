package rs485.secondarymonitor.secondjvm.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

public class ChatGui implements ISDMGui {
	
	public List<ChatLine> chatList = new ArrayList<ChatLine>(); //ChatContent
	private int scoll = 0; //Scroll
	private boolean field_73769_e = true; //Scroll bar color
	
	@Override
	public void renderGui(Minecraft mc) {
		int par1 = 0;
		int j = 20;
		int k = 0;
		int l = this.chatList.size();
		
		ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		if(l > 0) {
			float f1 = 1; // ChatScale
			int i1 = 10; // Chat Width
			GL11.glPushMatrix();
			GL11.glTranslatef(2.0F, scaledresolution.getScaledHeight() - 2.0F, 0.0F);
			GL11.glScalef(f1, f1, 1.0F);
			int j1;
			int k1;
			int l1;
			
			for(j1 = 0; j1 + this.scoll < this.chatList.size() && j1 < j; ++j1) {
				ChatLine chatline = (ChatLine)this.chatList.get(j1 + this.scoll);
				
				if(chatline != null) {
					k1 = par1 - chatline.getUpdatedCounter();
					
					double d0 = (double)k1 / 200.0D;
					d0 = 1.0D - d0;
					d0 *= 10.0D;
					
					if(d0 < 0.0D) {
						d0 = 0.0D;
					}
					
					if(d0 > 1.0D) {
						d0 = 1.0D;
					}
					
					d0 *= d0;
					l1 = (int)(255.0D * d0);
					
					l1 = 255;
					
					l1 = (int)((float)l1);
					++k;
					
					if(l1 > 3) {
						byte b0 = 0;
						int i2 = -j1 * 9;
						RenderHelper.drawRect(b0, i2 - 9, b0 + i1 + 4, i2, l1 / 2 << 24);
						GL11.glEnable(GL11.GL_BLEND);
						String s = chatline.getChatLineString();
						
						if(!mc.gameSettings.chatColours) {
							s = StringUtils.stripControlCodes(s);
						}
						
						mc.fontRenderer.drawStringWithShadow(s, b0, i2 - 8, 16777215 + (l1 << 24));
					}
					
				}
			}
			
			j1 = mc.fontRenderer.FONT_HEIGHT;
			GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
			int j2 = l * j1 + l;
			k1 = k * j1 + k;
			int k2 = this.scoll * k1 / l;
			int l2 = k1 * k1 / j2;
			
			if(j2 != k1) {
				l1 = k2 > 0 ? 170 : 96;
				int i3 = this.field_73769_e ? 13382451 : 3355562;
				RenderHelper.drawRect(0, -k2, 2, -k2 - l2, i3 + (l1 << 24));
				RenderHelper.drawRect(2, -k2, 1, -k2 - l2, 13421772 + (l1 << 24));
			}
			
			GL11.glPopMatrix();
		}
	}

	@Override
	public void handleMouseOverAt(int x, int y) {
		
	}
	
	@Override
	public void handleMouseClickAt(int x, int y) {
		
	}
}
