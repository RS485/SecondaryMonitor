package rs485.secondarymonitor.connection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.proxy.MainProxy;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ConsolePacketHandler {

	public static List<ConsolePacket> packetlist;
	public static boolean init;
	public static Map<Class<? extends ConsolePacket>, ConsolePacket> packetmap;

	@SuppressWarnings("unchecked")
	// Suppressed because this cast should never fail.
	public static <T extends ConsolePacket> T getPacket(Class<T> clazz) {
		return (T) packetmap.get(clazz).template();
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows({ IOException.class, InvocationTargetException.class, IllegalAccessException.class, InstantiationException.class })
	// Suppression+sneakiness because these shouldn't ever fail, and if they do, it needs to fail.
	public static final void intialize() {
		if(init) return;
		init = true;
		final List<ClassInfo> classes = new ArrayList<ClassInfo>(ClassPath.from(ConsolePacketHandler.class.getClassLoader()).getTopLevelClassesRecursive("rs485.secondarymonitor.connection.packets"));
		Collections.sort(classes, new Comparator<ClassInfo>() {
			@Override
			public int compare(ClassInfo o1, ClassInfo o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}
		});

		packetlist = new ArrayList<ConsolePacket>(classes.size());
		packetmap = new HashMap<Class<? extends ConsolePacket>, ConsolePacket>(classes.size());

		int currentid = 0;

		PrintWriter file = new PrintWriter(new FileOutputStream(new File("1-Packets-" + (MainProxy.isSecondJVM() ? "Snd" : "Fst") + ".dump")), true);
		for (ClassInfo c : classes) {
			final Class<?> cls = c.load();
			final ConsolePacket instance = (ConsolePacket) cls.getConstructors()[0].newInstance(currentid);
			packetlist.add(instance);
			packetmap.put((Class<? extends ConsolePacket>) cls, instance);
			file.write(currentid + ":\t" + cls.getSimpleName() + "\n");
			currentid++;
		}
		file.close();
	}

	public static void onPacketData(final ConsolePacket packet) {
		try {
			packet.processPacket();
		} catch(Exception e) {
			//LogisticsPipes.log.severe(packet.getClass().getName());
			//LogisticsPipes.log.severe(packet.toString());
			throw new RuntimeException(e);
		}
	}
}
