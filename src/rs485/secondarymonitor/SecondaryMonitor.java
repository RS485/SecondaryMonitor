package rs485.secondarymonitor;

/**
 * Feature TODO list:
 * Opis Map Overlay on second screen
 * Allow MiniMap full screen
 * Implement Minimap controlls
 * Waila support on Second display
 * Online Player List With entity rendering player coordinates and dimension
 * Item Tooltips for player inventory
 * Mod List
 * Modify Crash reports to clarify that this mod is experimental
 * Implement dynamic mod load system, to easily fix incompatible mods.
 * 
 * Feature Ideas (not decided whether they get implemented or not)
 * ComputerCraft peripheral for user specific information on screen. (Turtle peipheral)
 * Tabs
 */

import java.util.logging.Logger;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.log.LoggingOutStream;
import rs485.secondarymonitor.network.PacketHandler;
import rs485.secondarymonitor.proxy.MainProxy;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(name="Secondary Monitor Mod", modid = "SDM")
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels=SecondaryMonitor.SECONDARY_MONITOR_CHANNEL_NAME, packetHandler=PacketHandler.class)
public class SecondaryMonitor {
	
	public static final String SECONDARY_MONITOR_CHANNEL_NAME = "SDM";
	private Logger log;
	private Logger log2;
	public LoggingOutStream stream;
	
	@Instance(value="SDM")
	public static SecondaryMonitor mod;
	
	public SecondaryMonitor() {
		PacketHandler.intialize();
		ConsolePacketHandler.intialize();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLLog.makeLog("SDM");
		log = Logger.getLogger("SDM");
		log2 = Logger.getLogger("SecondMC");
		log2.setParent(log);
		stream = new LoggingOutStream(log2);
		MainProxy.proxy.init(this);
	}
}
