package rs485.secondarymonitor.connection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class OutputThread extends Thread {

	private Queue<byte[]> packets = new LinkedList<byte[]>();
	private DataOutputStream output;
	
	public OutputThread(OutputStream out) {
		if(out instanceof DataOutputStream) {
			this.output = (DataOutputStream) out;
		} else {
			this.output = new DataOutputStream(out);
		}
		this.setDaemon(true);
		this.setName("[SDM] Packet Output Thread");
		this.start();
	}
	
	public void queueData(final byte[] data) {
		new Thread() {
			public void run() {
				this.setName("[SDM] Compression Thread");
				byte[] compressed = CompressionHelper.compress(data);
				synchronized(packets) {
					packets.add(compressed);
				}				
			}
		}.start();
	}
	
	@Override
	public void run() {
		while(true) {
			boolean isEmpty;
			synchronized(packets) {
				isEmpty = packets.isEmpty();
			}
			if(isEmpty) {
				try {
					Thread.sleep(10);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				byte[] data;
				synchronized(packets) {
					data = packets.poll();
				}
				try {
					output.writeInt(data.length);
					output.write(data, 0, data.length);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
