package rs485.secondarymonitor.firstjvm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Resource;
import net.minecraft.util.ResourceLocation;
import rs485.secondarymonitor.SecondaryMonitor;
import rs485.secondarymonitor.connection.ISendConsolePacket;
import rs485.secondarymonitor.connection.InputThread;
import rs485.secondarymonitor.connection.KeepAliveThread;
import rs485.secondarymonitor.connection.OutputThread;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.firstjvm.proxy.MainProxy;
import rs485.secondarymonitor.secondjvm.asm.SDMSndGuiTweaker;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class JVMHandler implements ISendConsolePacket {
	private static JVMHandler instance;
	public static JVMHandler instance() {
		if(instance == null) {
			instance = new JVMHandler();
		}
		return instance;
	}
	
	@Getter 		private boolean isRunning = false;
	@Getter 		private final boolean isSecondJVM;
	@Getter @Setter	private boolean isJVMCompleted = false;
					private OutputThread sender;
	@Getter			private DataOutputStream senderPlayerData;
	@Getter	@Setter	private boolean isStarting = false;
	
	private JVMHandler() {
		boolean result = false;
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		for(String arg:arguments) {
			if(arg.contains("-DThisIsSecond")) {
				result = true;
				break;
			}
		}
		isSecondJVM = result;
	}

	@SuppressWarnings("resource")
	public void startSecondJVM() throws UnknownHostException, IOException {
		if(isSecondJVM()) return;
		if(isRunning()) throw new RuntimeException("Second JVM already running");
		isStarting = true;
		isJVMCompleted = false;
		setupSDMMCDIR();
		ServerSocket server = new ServerSocket(0, 0, InetAddress.getByName(null));
		int port = server.getLocalPort();
		ServerSocket serverPlayerData = new ServerSocket(0, 0, InetAddress.getByName(null));
		int portPlayerData = serverPlayerData.getLocalPort();
		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		List<String> startCommand = new LinkedList<String>();
		startCommand.add(path);
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		for(String arg:arguments) {
			if(arg.contains("java.library.path") || arg.contains("file.encoding") || arg.contains("-Xshare")) {
				startCommand.add(arg);
			}
		}
		startCommand.add("-DThisIsSecond");
		startCommand.add("-DSecondJVMPort="+port);
		startCommand.add("-DSecondJVMPortPlayerData="+portPlayerData);
		startCommand.addAll(Arrays.asList(new String[]{"-cp", classpath, "net.minecraft.launchwrapper.Launch", "--username", FMLClientHandler.instance().getClient().getSession().getUsername() + "-SDM", "--tweakClass", SDMSndGuiTweaker.class.getName()}));
		ProcessBuilder processBuilder = new ProcessBuilder(startCommand.toArray(new String[0]));
		processBuilder.redirectErrorStream(true);
		final Process process = processBuilder.start();
		Runtime.getRuntime().addShutdownHook(new Thread() { //Add Shutdown ensurement
			@Override public void run() {process.destroy();}
		});
		final DataInputStream stream = new DataInputStream(process.getInputStream());
		for(int i=0;i<100;i++) process.getOutputStream().write(120);
		new Thread() {
			private boolean running = true;
			public void run() {
				this.setName("[SDM] Console Relay Thread");
				PrintWriter writer = new PrintWriter(SecondaryMonitor.mod.stream, true);
				while(running) {
					try {
						@SuppressWarnings("deprecation")
						String line = stream.readLine();
						if(line == null) {
							System.err.println("[SDM] Lost Connection to second GUI.");
							JVMHandler.this.isRunning = false;
							JVMHandler.this.isStarting = false;
							return;
						}
						writer.println(line);
					} catch(IOException e1) {
						e1.printStackTrace();
					}
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		this.isRunning = true;
		Socket socket = server.accept();
		this.sender = new OutputThread(socket.getOutputStream());
		new InputThread(socket.getInputStream(), MainProxy.packetProcessor);
		Socket socketPlayerData = serverPlayerData.accept();
		senderPlayerData = new DataOutputStream(socketPlayerData.getOutputStream());
		new KeepAliveThread(this);
	}

	private void setupSDMMCDIR() throws IOException {
		File mcDir = Minecraft.getMinecraft().mcDataDir;
		File SDM_mcDir = new File(Minecraft.getMinecraft().mcDataDir, "SDM_mcDir");
		try {
			//FileUtils.deleteDirectory(SDM_mcDir);
		} catch(Exception e) {}
		SDM_mcDir.mkdir();
		copyFolder(new File(mcDir, "config"), new File(SDM_mcDir, "config"), false);
		Resource resource = Minecraft.getMinecraft().mcResourceManager.getResource(new ResourceLocation("secondarymonitor", "textures/icon.png"));
        InputStream in = resource.getInputStream();
        OutputStream out = new FileOutputStream(new File(SDM_mcDir, "icon.png"));
		byte[] buffer = new byte[1024];
		int length;
		while((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}
	
	private static void copyFolder(File src, File dest, boolean overwrite) throws IOException {
		if(src.isDirectory()) {
			if(!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for(String file: files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile, overwrite);
			}
		} else {
			if(dest.exists() && !overwrite) return;
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}

	@Override
	public void sendConsolePacket(ConsolePacket packet) {
		packet.create();
		sendConsolePacket(packet.getData());
	}

	public void sendConsolePacket(byte[] data) {
		if(sender != null) {
			sender.queueData(data);
		}
	}

	@Override
	public boolean isActive() {
		return this.isRunning;
	}
	
	public void remoteShutdown() {
		//FMLClientHandler.instance().getClient().shutdown();
		this.isJVMCompleted = false;
		this.isRunning = false;
		this.sender = null;
		this.senderPlayerData = null;
	}
}
