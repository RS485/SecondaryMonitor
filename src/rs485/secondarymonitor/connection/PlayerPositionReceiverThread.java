package rs485.secondarymonitor.connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import rs485.secondarymonitor.secondjvm.Main;

public class PlayerPositionReceiverThread extends Thread {
	
	private final DataInputStream in;
	private final Socket socket;
	
	public PlayerPositionReceiverThread(int port) throws UnknownHostException, IOException {
		super();
		socket = new Socket(InetAddress.getByName(null), port);
		DataInputStream dataInput = new DataInputStream(socket.getInputStream());
		in = new DataInputStream(dataInput);
		this.setDaemon(true);
		this.setName("[SDM] Player Position Receiver Thread");
		this.start();
	}
	
	public void run() {
		while(true) {
			try {
				double posX = in.readDouble();
				Main.instance().playerwriteLock.lock();
				Main.instance().getPlayer().posX = posX;
				Main.instance().getPlayer().posY = in.readDouble();
				Main.instance().getPlayer().posZ = in.readDouble();
				Main.instance().getPlayer().rotationYaw = in.readFloat();
				Main.instance().getPlayer().rotationPitch = in.readFloat();
				Main.instance().getPlayer().setLocationAndAngles(Main.instance().getPlayer().posX, Main.instance().getPlayer().posY, Main.instance().getPlayer().posZ, Main.instance().getPlayer().rotationYaw, Main.instance().getPlayer().rotationPitch);
				Main.instance().playerwriteLock.unlock();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}	
}
