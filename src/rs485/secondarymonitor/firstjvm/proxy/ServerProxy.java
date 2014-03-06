package rs485.secondarymonitor.firstjvm.proxy;

import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.firstjvm.tick.ServerTickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ServerProxy implements IProxy {

	@Override
	public void init(SecondaryMonitor mod) {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	@Override
	public void updatePlayerData() {
		
	}
}
