package rs485.secondarymonitor.connection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
		this.setName("[SDM] Packet Input Thread");
		this.start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				final int length = in.readInt();
				final byte[] buffer = new byte[length];
				int current = 0;
				while(current < length) {
					int result = in.read(buffer, current, length - current);
					current += result;
				}
				new Thread() {
					public void run() {
						this.setName("[SDM] Decompression Thread");
						byte[] result = CompressionHelper.decompress(buffer);
						try {
							handler.handlePacket(new DataInputStream(new ByteArrayInputStream(result)));
						} catch(Exception e) {
							e.printStackTrace();
							System.out.println(length);
							System.out.println(Arrays.toString(buffer));
						}
					}
				}.start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
