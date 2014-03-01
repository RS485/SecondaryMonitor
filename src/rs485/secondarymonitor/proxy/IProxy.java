package rs485.secondarymonitor.proxy;

import rs485.secondarymonitor.SecondaryMonitor;

public interface IProxy {
	public void init(SecondaryMonitor mod);
	public boolean isSecondJVM();
	public void updatePlayerData();
}
