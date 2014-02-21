package rs485.secondarymonitor.secondjvm.asm;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import cpw.mods.fml.common.launcher.FMLTweaker;

public class SDMSecondGuiTweaker extends FMLTweaker {
	
	public SDMSecondGuiTweaker() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> clazz = Launch.classLoader.findClass("rs485.secondarymonitor.secondjvm.console.InputOutputHelper");
		clazz.getMethod("instance").invoke(null);
	}
	
	@Override
	public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
		super.acceptOptions(args, gameDir, assetsDir, profile);
	}

	@Override
	public void injectIntoClassLoader(LaunchClassLoader classLoader) {
		classLoader.registerTransformer(SDMSecondGuiClassTransformer.class.getName());
		super.injectIntoClassLoader(classLoader);
	}
}
