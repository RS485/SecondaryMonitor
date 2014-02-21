package rs485.secondarymonitor.tick;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.gui.ChatLine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.ChangeMouseDisplayStatePacket;
import rs485.secondarymonitor.connection.packets.ChatContentPacket;
import rs485.secondarymonitor.connection.packets.MouseDeltaPacket;
import rs485.secondarymonitor.proxy.ClientProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class RenderTickHandler implements ITickHandler {

	private final SecondaryMonitor mod;
	private ClientProxy proxy;
	private List<ChatLine> oldLines = new ArrayList<ChatLine>();
	private boolean isMouseDisplay = false;
	
	public RenderTickHandler(SecondaryMonitor mod, ClientProxy proxy) {
		this.mod = mod;
		this.proxy = proxy;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if(Keyboard.isKeyDown(Keyboard.KEY_Y)) {
			int deltaX = Mouse.getDX();
			int deltaY = Mouse.getDY();
			proxy.sendConsolePacket(ConsolePacketHandler.getPacket(MouseDeltaPacket.class).setMouseX(deltaX).setMouseY(deltaY));
			if(!isMouseDisplay) {
				isMouseDisplay = true;
				proxy.sendConsolePacket(ConsolePacketHandler.getPacket(ChangeMouseDisplayStatePacket.class).setDisplayMouse(true));
			}
		} else {
			if(isMouseDisplay) {
				isMouseDisplay = false;
				proxy.sendConsolePacket(ConsolePacketHandler.getPacket(ChangeMouseDisplayStatePacket.class).setDisplayMouse(false));
			}
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		List<ChatLine> chatLines = FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().field_96134_d;
		if(checkForChange(chatLines, oldLines)) {
			oldLines.clear();
			for(int i=0;i<chatLines.size();i++) {
				oldLines.add(chatLines.get(i));
			}
			proxy.sendConsolePacket(ConsolePacketHandler.getPacket(ChatContentPacket.class).setLines(oldLines));
		}
	}

	private boolean checkForChange(List<ChatLine> newList, List<ChatLine> oldList) {
		if(newList.size() != oldList.size()) return true;
		for(int i=0;i<newList.size();i++) {
			if(!newList.get(i).equals(oldList.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "Secondary Monitor Mod Render Tick";
	}	
}
