package rs485.secondarymonitor.secondjvm.asm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.launcher.FMLTweaker;
import cpw.mods.fml.relauncher.CoreModManager;

public class SDMSecondGuiTweaker extends FMLTweaker {
	
	public SDMSecondGuiTweaker() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> clazz = Launch.classLoader.findClass("rs485.secondarymonitor.secondjvm.InputOutputHelper");
		clazz.getMethod("instance").invoke(null);
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer(SDMSecondGuiClassTransformer.class.getName());
		super.injectIntoClassLoader(classLoader);
		try {
			Method discover = CoreModManager.class.getDeclaredMethod("discoverCoreMods", File.class, LaunchClassLoader.class);
			discover.setAccessible(true);
			discover.invoke(null, super.getGameDir(), classLoader);
		} catch(NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch(SecurityException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch(IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public File getGameDir() {
		new File(super.getGameDir(), "SDM_mcDir").mkdir();
		return new File(super.getGameDir(), "SDM_mcDir");
	}

	@Override
	public String[] getLaunchArguments() {
		List<String> args = new ArrayList<String>();
		args.addAll(Arrays.asList(super.getLaunchArguments()));
		Iterator<String> i = args.iterator();
		while(i.hasNext()) {
			if("--gameDir".equals(i.next())) {
				i.remove();
				i.remove();
			}
		}
		args.add("--gameDir");
		args.add(getGameDir().getAbsolutePath());
		return args.toArray(new String[args.size()]);
	}
}
