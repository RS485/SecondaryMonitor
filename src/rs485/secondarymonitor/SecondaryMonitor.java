package rs485.secondarymonitor;

import java.io.IOException;
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
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels="SDM", packetHandler=PacketHandler.class)
public class SecondaryMonitor {
	
	private Logger log;
	private Logger log2;
	private LoggingOutStream stream;
	
	@Instance
	public static SecondaryMonitor mod;

	public SecondaryMonitor() {
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
	
	public void logFromSecondMC(byte[] bytes) {
		try {
			stream.write(bytes);
			stream.flush();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
