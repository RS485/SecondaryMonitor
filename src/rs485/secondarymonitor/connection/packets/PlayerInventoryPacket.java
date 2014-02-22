package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.secondjvm.Main;
import cpw.mods.fml.client.FMLClientHandler;

@Accessors(chain=true)
public class PlayerInventoryPacket extends ConsolePacket {

	@Getter
	@Setter
	private ItemStack[] armor;
	
	@Getter
	@Setter
	private ItemStack[] inventory;
	
	public PlayerInventoryPacket(int id) {
		super(id);
	}

	@Override
	public boolean needMainThread() {
		return true;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		int length = data.readInt();
		armor = new ItemStack[length];
		for(int i=0;i<length;i++) {
			armor[i] = readItemStack(data);
		}
		length = data.readInt();
		inventory = new ItemStack[length];
		for(int i=0;i<length;i++) {
			inventory[i] = readItemStack(data);
		}
	}
	
	private ItemStack readItemStack(DataInputStream data) throws IOException {
		if(data.readBoolean()) {
			ItemStack stack = new ItemStack(data.readInt(),data.readInt(),data.readInt());
			if(data.readBoolean()) {
				stack.setTagCompound((NBTTagCompound) NBTBase.readNamedTag(data));
			}
			return stack;
		}
		return null;
	}
	
	@Override
	public void processPacket() {
		for(int i=0;i<armor.length;i++) {
			Main.instance().getPlayer().inventory.armorInventory[i] = armor[i];
		}
		for(int i=0;i<inventory.length;i++) {
			Main.instance().getPlayer().inventory.mainInventory[i] = inventory[i];
		}
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(armor.length);
		for(int i=0;i<armor.length;i++) {
			writeItemStack(data, armor[i]);
		}
		data.writeInt(inventory.length);
		for(int i=0;i<inventory.length;i++) {
			writeItemStack(data, inventory[i]);
		}
	}
	
	private void writeItemStack(DataOutputStream data, ItemStack stack) throws IOException {
		if(stack == null) {
			data.writeBoolean(false);
		} else {
			data.writeBoolean(true);
			data.writeInt(stack.itemID);
			data.writeInt(stack.stackSize);
			data.writeInt(stack.getItemDamage());
			NBTTagCompound nbt = stack.getTagCompound();
			if(nbt == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				NBTBase.writeNamedTag(nbt, data);
			}
		}
	}
	
	@Override
	public ConsolePacket template() {
		return new PlayerInventoryPacket(getId());
	}
}
