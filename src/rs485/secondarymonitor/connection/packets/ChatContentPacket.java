package rs485.secondarymonitor.connection.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.ChatLine;
import rs485.secondarymonitor.connection.abstractpackets.ConsolePacket;
import rs485.secondarymonitor.secondjvm.Main;

@Accessors(chain=true)
public class ChatContentPacket extends ConsolePacket {
	
	@Getter
	@Setter
	private List<ChatLine> lines;
	
	public ChatContentPacket(int id) {
		super(id);
	}

	@Override
	public boolean needMainThread() {
		return true;
	}
	
	@Override
	public void readData(DataInputStream data) throws IOException {
		int length = data.readInt();
		lines = new ArrayList<ChatLine>(length);
		for(int i=0;i<length;i++) {
			if(data.readBoolean()) {
				lines.add(new ChatLine(data.readInt(),data.readUTF(),data.readInt()));
			}
		}
	}
	
	@Override
	public void processPacket() {
		Main.instance().chatGui.chatList.clear();
		Main.instance().chatGui.chatList.addAll(getLines());
	}
	
	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(lines.size());
		for(int i=0;i<lines.size();i++) {
			ChatLine line = lines.get(i);
			if(line == null) {
				data.writeBoolean(false);
			} else {
				data.writeBoolean(true);
				data.writeInt(line.getUpdatedCounter());
				data.writeUTF(line.getChatLineString());
				data.writeInt(line.getChatLineID());
			}
		}
	}
	
	@Override
	public ConsolePacket template() {
		return new ChatContentPacket(getId());
	}
}
