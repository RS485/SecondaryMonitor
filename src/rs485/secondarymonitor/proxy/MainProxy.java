package rs485.secondarymonitor.proxy;

import cpw.mods.fml.common.SidedProxy;

public class MainProxy {
	
	@SidedProxy(clientSide="rs485.secondarymonitor.proxy.ClientProxy", serverSide="rs485.secondarymonitor.proxy.ServerProxy")
	public static IProxy proxy;
	
}
