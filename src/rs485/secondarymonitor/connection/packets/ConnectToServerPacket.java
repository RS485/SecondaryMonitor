package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.proxy.ClientProxy;
import rs485.secondarymonitor.proxy.MainProxy;

@Accessors(chain=true)
public class ConnectToServerPacket extends ConsolePacket {
	
	public ConnectToServerPacket(int id) {
		super(id);
	}

	@Getter
	@Setter
	private int port;
	
	@Getter
	@Setter
	private int ident;
	
	@Override
	public boolean needMainThread() {
		return false;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		port = data.readInt();
		ident = data.readInt();
	}
	
	@Override
	public void processPacket() {
		try {
			@SuppressWarnings("resource")
			Socket socket = new Socket(InetAddress.getByName(null), getPort());
			new DataOutputStream(socket.getOutputStream()).writeInt(ident);
			((ClientProxy)MainProxy.proxy).setOutputSocket(socket.getOutputStream());
		} catch(UnknownHostException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(port);
		data.writeInt(ident);
	}
	
	@Override
	public ConsolePacket template() {
		return new ConnectToServerPacket(getId());
	}	
}
