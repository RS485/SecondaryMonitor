package rs485.secondarymonitor.secondjvm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.ISendConsolePacket;
import rs485.secondarymonitor.connection.InputThread;
import rs485.secondarymonitor.connection.KeepAliveThread;
import rs485.secondarymonitor.connection.OutputThread;
import rs485.secondarymonitor.connection.PlayerPositionReceiverThread;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.connection.packets.GuiStartedPacket;
import rs485.secondarymonitor.firstjvm.proxy.MainProxy;

public class InputOutputHelper implements ISendConsolePacket {

	private OutputThread output;
	
	public InputOutputHelper() {
		//PrintStream newOutputStream = new PrintStream(new OutputStreamRelay(512), true);
		//System.setErr(newOutputStream);
		ConsolePacketHandler.intialize();
		connectToMainGui();
	}
	
	private void connectToMainGui() {
		try {
			RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
			List<String> arguments = runtimeMxBean.getInputArguments();
			int port = 0;
			int portPlayerData = 0;
			for(String arg:arguments) {
				if(arg.startsWith("-DSecondJVMPort=")) {
					port = Integer.valueOf(arg.substring(16));
				}
				if(arg.startsWith("-DSecondJVMPortPlayerData=")) {
					portPlayerData = Integer.valueOf(arg.substring(26));
				}
			}
			@SuppressWarnings("resource")
			Socket socket = new Socket(InetAddress.getByName(null), port);
			DataInputStream dataInput = new DataInputStream(socket.getInputStream());
			DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
			new InputThread(dataInput, MainProxy.packetProcessor);
			output = new OutputThread(dataOut);
			new KeepAliveThread(this);
			new PlayerPositionReceiverThread(portPlayerData);
		} catch(UnknownHostException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void gameStarted() {
		sendToMain(ConsolePacketHandler.getPacket(GuiStartedPacket.class));
	}

	@Override
	public void sendConsolePacket(ConsolePacket packet) {
		sendToMain(packet);
	}

	public void sendToMain(ConsolePacket packet) {
		packet.create();
		byte[] data = packet.getData();
		output.queueData(data);
	}

	@Override
	public boolean isActive() {
		return output != null;
	}
	
	public static InputOutputHelper instance() {
		if(instance == null) {
			instance = new InputOutputHelper();
			System.out.println(instance.getClass().getClassLoader());
		}
		return instance;
	}
	
	private static InputOutputHelper instance;
}
