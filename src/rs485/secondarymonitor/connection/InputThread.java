package rs485.secondarymonitor.connection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputThread extends Thread {

	private final DataInputStream in;
	private final IHandlePacket handler;
	
	public InputThread(InputStream in, IHandlePacket handler) {
		if(in == null || handler == null) throw new NullPointerException();
		if(in instanceof DataInputStream) {
			this.in = (DataInputStream)in;
		} else {
			this.in = new DataInputStream(in);
		}
		this.handler = handler;
		this.setDaemon(true);
		this.start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				int length;
				length = in.readInt();
				try {
					byte[] buffer = new byte[length];
					in.read(buffer);
					handler.handlePacket(new DataInputStream(new ByteArrayInputStream(buffer)));
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
