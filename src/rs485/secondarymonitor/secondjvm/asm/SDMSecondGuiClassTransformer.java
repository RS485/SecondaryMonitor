package rs485.secondarymonitor.secondjvm.asm;

import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SDMSecondGuiClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(name.equals("net.minecraft.client.Minecraft")) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			for(MethodNode m:node.methods) {
				if(m.name.equals("runGameLoop")) { //TODO: srgName
					node.methods.remove(m);
					break;
				}
			}
			for(MethodNode m:node.methods) {
				if(m.name.equals("startGame")) { //TODO: srgName
					handleStartGameMethod(m);
					break;
				}
			}
			//Add new runGameLoop Method (Redirect to rs485.secondarymonitor.secondjvm.Main)
			{
				MethodVisitor mv = node.visitMethod(Opcodes.ACC_PRIVATE, "runGameLoop", "()V", null, null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitVarInsn(Opcodes.ALOAD, 0);
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "rs485/secondarymonitor/secondjvm/Main", "instance", "(Lnet/minecraft/client/Minecraft;)Lrs485/secondarymonitor/secondjvm/Main;");
				mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "rs485/secondarymonitor/secondjvm/Main", "runGameLoop", "()V");
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitInsn(Opcodes.RETURN);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLocalVariable("this", "Lnet/minecraft/client/Minecraft;", null, l0, l2, 0);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else {
			return bytes;
		}
	}

	private void handleStartGameMethod(MethodNode m) {
		ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();
		AbstractInsnNode old = null;
		while(iterator.hasNext()) {
			AbstractInsnNode node = iterator.next();
			if(node instanceof MethodInsnNode) {
				if(node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode)node).owner.equals("org/lwjgl/opengl/Display") && ((MethodInsnNode)node).name.equals("setTitle") && ((MethodInsnNode)node).desc.equals("(Ljava/lang/String;)V")) {
					if(old instanceof LdcInsnNode) {
						((LdcInsnNode)old).cst = "Seondary Monitor" + (((LdcInsnNode)old).cst instanceof String ? " " + (String)((LdcInsnNode)old).cst : "");
					}
					return;
				}
			}
			old = node;
		}
	}
}
