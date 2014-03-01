package rs485.secondarymonitor.connection;

import rs485.secondarymonitor.connection.packets.KeepAlivePacket;

public class KeepAliveThread extends Thread {
	private ISendConsolePacket sender;
	public KeepAliveThread(ISendConsolePacket sender) {
		this.sender = sender;
		this.setDaemon(true);
		this.setName("[SDM] Keep Alive Thread");
		this.start();
	}
	
	@Override
	public void run() {
		while(true) {
			sender.sendConsolePacket(ConsolePacketHandler.getPacket(KeepAlivePacket.class));
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
