package rs485.secondarymonitor.secondjvm.console;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.ISendConsolePacket;
import rs485.secondarymonitor.connection.InputThread;
import rs485.secondarymonitor.connection.KeepAliveThread;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.connection.packets.ConnectToServerPacket;
import rs485.secondarymonitor.connection.packets.GuiStartedPacket;
import rs485.secondarymonitor.proxy.ClientProxy;
import rs485.secondarymonitor.proxy.MainProxy;

public class InputOutputHelper implements ISendConsolePacket {

	private final DataOutputStream dataOut;
	private DataInputStream dataInput;
	private final PrintStream newOutputStream;
	private boolean init;
	
	public InputOutputHelper() {
		System.out.println(System.in);
		try {
			System.out.println("Available: " + System.in.available());
		} catch(IOException e) {
			e.printStackTrace();
		}
		dataOut = new DataOutputStream(System.err);
		newOutputStream = new PrintStream(new OutputStreamRelay(512), true);
		System.setErr(newOutputStream);
		ConsolePacketHandler.intialize();
	}

	public void gameStarted() {
		try {
			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(0, 0, InetAddress.getByName(null));
			int port = server.getLocalPort();
			boolean connected = false;
			while(!connected) {
				int ident = new Random().nextInt();
				sendToMain(ConsolePacketHandler.getPacket(ConnectToServerPacket.class).setPort(port).setIdent(ident));
				Socket socket = server.accept();
				dataInput = new DataInputStream(socket.getInputStream());
				int newIdent = dataInput.readInt();
				if(newIdent != ident) {
					System.out.println("Couldn't identify Connection");
					socket.close();
				} else {
					connected = true;
					new InputThread(dataInput, ((ClientProxy)MainProxy.proxy).packetProcessor);
				}
			}
		} catch(UnknownHostException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
		sendToMain(ConsolePacketHandler.getPacket(GuiStartedPacket.class));
	}

	@Override
	public void sendConsolePacket(ConsolePacket packet) {
		sendToMain(packet);
	}

	public void sendToMain(ConsolePacket packet) {
		if(!init) {
			new PrintStream(dataOut).println("ACTIVATED-REPLACEMENT, (Code:1)");
			new KeepAliveThread(this);
			init = true;
		}
		packet.create();
		byte[] data = packet.getData();
		try {
			dataOut.writeInt(data.length);
			dataOut.write(data);
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
