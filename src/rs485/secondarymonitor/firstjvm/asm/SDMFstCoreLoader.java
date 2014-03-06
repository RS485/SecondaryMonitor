package rs485.secondarymonitor.firstjvm.asm;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class SDMFstCoreLoader implements IFMLLoadingPlugin {
	
	@Override
	@Deprecated
	public String[] getLibraryRequestClass() {
		return null;
	}
	
	@Override
	public String[] getASMTransformerClass() {
		boolean isSecondJVM = false;
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		for(String arg:arguments) {
			if(arg.contains("-DThisIsSecond")) {
				isSecondJVM = true;
				break;
			}
		}
		if(!isSecondJVM) {
			return new String[]{SDMFstClassTransformer.class.getName()};
		} else {
			return new String[]{};
		}
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {}
}
