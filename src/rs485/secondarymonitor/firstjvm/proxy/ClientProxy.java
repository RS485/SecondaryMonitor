package rs485.secondarymonitor.firstjvm.proxy;

import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.firstjvm.JVMHandler;
import rs485.secondarymonitor.firstjvm.renderer.MinecraftInfoRenderer;
import rs485.secondarymonitor.firstjvm.tick.ClientTickHandler;
import rs485.secondarymonitor.firstjvm.tick.ServerTickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy implements IProxy {

	
	@Override
	public void init(SecondaryMonitor mod) {
		MinecraftInfoRenderer.init();
		TickRegistry.registerTickHandler(new ClientTickHandler(mod, this), Side.CLIENT);
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
		TickRegistry.registerTickHandler(MainProxy.packetProcessor, Side.CLIENT);
	}
	
	@Override
	public void updatePlayerData() {
		DataOutputStream senderPlayerData = JVMHandler.instance().getSenderPlayerData();
		if(Minecraft.getMinecraft().thePlayer == null || senderPlayerData == null) return;
		try {
			senderPlayerData.writeDouble(Minecraft.getMinecraft().thePlayer.posX);
			senderPlayerData.writeDouble(Minecraft.getMinecraft().thePlayer.posY);
			senderPlayerData.writeDouble(Minecraft.getMinecraft().thePlayer.posZ);
			senderPlayerData.writeFloat(Minecraft.getMinecraft().thePlayer.rotationYaw);
			senderPlayerData.writeFloat(Minecraft.getMinecraft().thePlayer.rotationPitch);
		} catch(IOException e) {
			e.printStackTrace();
			senderPlayerData = null;
			//TODO Reconnect
		}
	}
}
