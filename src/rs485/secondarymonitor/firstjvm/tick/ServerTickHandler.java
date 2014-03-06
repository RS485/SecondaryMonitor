package rs485.secondarymonitor.firstjvm.tick;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import rs485.secondarymonitor.connection.ConsolePacketHandler;
import rs485.secondarymonitor.connection.packets.PlayerInventoryPacket;
import rs485.secondarymonitor.firstjvm.proxy.MainProxy;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.Player;

public class ServerTickHandler implements ITickHandler {
	
	@Data
	private class InvCache {
		private ItemStack[] armor;
		private ItemStack[] inventory;
	}
	
	private Map<String, InvCache> inventoryCache = new HashMap<String, InvCache>();
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		for(World world: DimensionManager.getWorlds()) {
			for(Object player:world.playerEntities) {
				handlePlayer((EntityPlayerMP) player);
			}
		}
	}
	
	private void handlePlayer(EntityPlayerMP entity) {
		InvCache cache = inventoryCache.get(entity.username);
		if(cache == null) {
			cache = new InvCache();
			cache.armor = new ItemStack[entity.inventory.armorInventory.length];
			for(int i=0;i<cache.armor.length;i++) {
				cache.armor[i] = ItemStack.copyItemStack(entity.inventory.armorInventory[i]);
			}
			cache.inventory = new ItemStack[entity.inventory.mainInventory.length];
			for(int i=0;i<cache.inventory.length;i++) {
				cache.inventory[i] = ItemStack.copyItemStack(entity.inventory.mainInventory[i]);
			}
			inventoryCache.put(entity.username, cache);
			MainProxy.sendPacketToPlayer(ConsolePacketHandler.getPacket(PlayerInventoryPacket.class).setArmor(cache.armor).setInventory(cache.inventory), (Player)entity);
			return;
		}
		boolean changed = false;
		for(int i=0;i<cache.armor.length;i++) {
			if(!ItemStack.areItemStacksEqual(cache.armor[i], entity.inventory.armorInventory[i])) {
				changed = true;
				cache.armor[i] = ItemStack.copyItemStack(entity.inventory.armorInventory[i]);
			}
		}
		for(int i=0;i<cache.inventory.length;i++) {
			if(!ItemStack.areItemStacksEqual(cache.inventory[i], entity.inventory.mainInventory[i])) {
				changed = true;
				cache.inventory[i] = ItemStack.copyItemStack(entity.inventory.mainInventory[i]);
			}
		}
		if(changed) {
			MainProxy.sendPacketToPlayer(ConsolePacketHandler.getPacket(PlayerInventoryPacket.class).setArmor(cache.armor).setInventory(cache.inventory), (Player)entity);
		}
	}
	
	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}
	
	@Override
	public String getLabel() {
		return "SDM Server Tick";
	}
}
