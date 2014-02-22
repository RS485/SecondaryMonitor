package rs485.secondarymonitor.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.connection.ISendConsolePacket;
import rs485.secondarymonitor.connection.InputThread;
import rs485.secondarymonitor.connection.KeepAliveThread;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.renderer.MinecraftInfoRenderer;
import rs485.secondarymonitor.secondjvm.asm.SDMSecondGuiTweaker;
import rs485.secondarymonitor.tick.PacketProcessorTick;
import rs485.secondarymonitor.tick.ClientTickHandler;
import rs485.secondarymonitor.tick.ServerTickHandler;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy implements IProxy, ISendConsolePacket {

	public PacketProcessorTick packetProcessor;
	private boolean isSecondJVM = false;
	public static boolean waitFor = true;
	private DataOutputStream sender;
	
	@Override
	public void init(SecondaryMonitor mod) {
		MinecraftInfoRenderer.init();
		TickRegistry.registerTickHandler(new ClientTickHandler(mod, this), Side.CLIENT);
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
		packetProcessor = new PacketProcessorTick();
		TickRegistry.registerTickHandler(packetProcessor, Side.CLIENT);
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		for(String arg:arguments) {
			if(arg.contains("-DThisIsSecond")) {
				isSecondJVM = true;
				break;
			}
		}
		if(!isSecondJVM) {
			startSecondJVM(arguments);
		}
	}

	@Override
	public boolean isSecondJVM() {
		return isSecondJVM;
	}

	private void startSecondJVM(List<String> arguments) {
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		List<String> startCommand = new LinkedList<String>();
		startCommand.add(path);
		for(String arg:arguments) {
			if(arg.contains("java.library.path") || arg.contains("file.encoding") || arg.contains("-Xshare")) {
				startCommand.add(arg);
			}
		}
		startCommand.add("-DThisIsSecond");
		startCommand.addAll(Arrays.asList(new String[]{"-cp", classpath, "net.minecraft.launchwrapper.Launch", "--username", FMLClientHandler.instance().getClient().getSession().getUsername() + "-SDM", "--tweakClass", SDMSecondGuiTweaker.class.getName()}));
		ProcessBuilder processBuilder = new ProcessBuilder(startCommand.toArray(new String[0]));
		processBuilder.redirectErrorStream(true);
		try {
			final Process process = processBuilder.start();
			Runtime.getRuntime().addShutdownHook(new Thread() { //Add Shutdown ensurement
				@Override public void run() {process.destroy();}
			});
			final DataInputStream stream = new DataInputStream(process.getInputStream());
			for(int i=0;i<100;i++) process.getOutputStream().write(120);
			//sender = new DataOutputStream(process.getOutputStream());
			new Thread() {
				private boolean running = true;
				public void run() {
					while(running) {
						try {
							@SuppressWarnings("deprecation")
							String line = stream.readLine();
							System.out.println(line);
							if(line == null) {
								System.err.println("Lost Connection to second GUI.");
								return;
							}
							if(line.contains("ACTIVATED-REPLACEMENT, (Code:1)")) {
								running = false;
								break;
							}
						} catch(IOException e1) {
							e1.printStackTrace();
						}
						try {
							Thread.sleep(10);
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					new InputThread(process.getInputStream(), packetProcessor);
				}
			}.start();
			waitFor = true;
			while(waitFor) {
				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
			new KeepAliveThread(this);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendConsolePacket(ConsolePacket packet) {
		packet.create();
		sendConsolePacket(packet.getData());
	}

	public void sendConsolePacket(byte[] data) {
		try {
			sender.writeInt(data.length);
			sender.write(data);
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void setOutputSocket(OutputStream outputStream) {
		sender = new DataOutputStream(outputStream);
	}
}
