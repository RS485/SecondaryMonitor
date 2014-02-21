package rs485.secondarymonitor.secondjvm.console;

import java.io.IOException;
import java.io.OutputStream;

import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.SystemOutputPacket;

public class OutputStreamRelay extends OutputStream {
	protected byte	buf[];
	protected int	count;
	
	public OutputStreamRelay(int size) {
		if(size <= 0) { throw new IllegalArgumentException("Buffer size <= 0"); }
		buf = new byte[size];
	}
	
	private void sendData(byte[] array, int from, int len) {
		byte[] data = new byte[len];
		System.arraycopy(array, from, data, 0, len);
		InputOutputHelper.instance().sendToMain(ConsolePacketHandler.getPacket(SystemOutputPacket.class).setMsg(data));
	}
	
	private void flushBuffer() throws IOException {
		if(count > 0) {
			sendData(buf, 0, count);
			count = 0;
		}
	}
	
	public synchronized void write(int b) throws IOException {
		if(count >= buf.length) {
			flushBuffer();
		}
		buf[count++] = (byte)b;
	}
	
	public synchronized void write(byte b[], int off, int len) throws IOException {
		if(len >= buf.length) {
			/*
			 * If the request length exceeds the size of the output buffer,
			 * flush the output buffer and then write the data directly.
			 * In this way buffered streams will cascade harmlessly.
			 */
			flushBuffer();
			sendData(b, off, len);
			return;
		}
		if(len > buf.length - count) {
			flushBuffer();
		}
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}
	
	public synchronized void flush() throws IOException {
		flushBuffer();
	}

	@Override
	public void close() throws IOException {
		flushBuffer();
		super.close();
	}
}
