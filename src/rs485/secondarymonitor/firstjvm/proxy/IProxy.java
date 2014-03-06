package rs485.secondarymonitor.firstjvm.proxy;

import rs485.secondarymonitor.SecondaryMonitor;

public interface IProxy {
	public void init(SecondaryMonitor mod);
	public void updatePlayerData();
}
