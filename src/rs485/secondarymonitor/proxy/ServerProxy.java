package rs485.secondarymonitor.proxy;

import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.tick.ServerTickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ServerProxy implements IProxy {

	@Override
	public void init(SecondaryMonitor mod) {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	@Override
	public boolean isSecondJVM() {
		return false;
	}
	
}
