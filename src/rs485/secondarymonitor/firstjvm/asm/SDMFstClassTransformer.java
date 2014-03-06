package rs485.secondarymonitor.firstjvm.asm;

import lombok.libs.org.objectweb.asm.Opcodes;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class SDMFstClassTransformer implements IClassTransformer {
	
	private enum ASMState {
		WAITING,
		CHANGING,
		DONE;
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if(name.equals("mapwriter.region.Region")) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode() {

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					if(access == Opcodes.ACC_PUBLIC && name.equals("<init>") && desc.equals("(Lmapwriter/region/RegionManager;IIII)V")) {
						MethodNode mn = new MethodNode(access, name, desc, signature, exceptions) {
							ASMState changing = ASMState.WAITING;
							
							@Override
							public void visitTypeInsn(int opcode, String type) {
								if(changing == ASMState.WAITING && opcode == Opcodes.NEW && "mapwriter/region/SurfacePixels".equals(type)) {
									type = "rs485/secondarymonitor/firstjvm/mapwriter/SDMFstSurfacePixels";
									changing = ASMState.CHANGING;
								}
								super.visitTypeInsn(opcode, type);
							}

							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc) {
								if(changing == ASMState.CHANGING) {
									if(opcode == Opcodes.INVOKESPECIAL && "mapwriter/region/SurfacePixels".equals(owner) && "<init>".equals(name) && "(Lmapwriter/region/Region;Ljava/io/File;)V".equals(desc)) {
										owner = "rs485/secondarymonitor/firstjvm/mapwriter/SDMFstSurfacePixels";
									}
									changing = ASMState.DONE;
								}
								super.visitMethodInsn(opcode, owner, name, desc);
							}
							
						};
				        methods.add(mn);
				        return mn;
					} else {
						return super.visitMethod(access, name, desc, signature, exceptions);
					}
				}
				
			};
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else if(name.equals("net.minecraft.client.gui.GuiMainMenu")) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode() {

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					if(access == Opcodes.ACC_PUBLIC && name.equals("initGui") && desc.equals("()V")) { //TODO srgNames
						MethodNode mn = new MethodNode(access, name, desc, signature, exceptions) {
							boolean codeAdded = false;
							int tryCatch = 0;
							
							@Override
							public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
								tryCatch++;
								super.visitTryCatchBlock(start, end, handler, type);
							}

							@Override
							public void visitLabel(Label label) {
								if(tryCatch >= 2 && !codeAdded) {
									codeAdded = true;
									Label l = new Label();
									super.visitLabel(l);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu", "buttonList", "Ljava/util/List;");
									super.visitTypeInsn(Opcodes.NEW, "rs485/secondarymonitor/firstjvm/gui/GuiButtonStartSndJVM");
									super.visitInsn(Opcodes.DUP);
									super.visitIntInsn(Opcodes.SIPUSH, 1201);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu", "width", "I");
									super.visitInsn(Opcodes.ICONST_2);
									super.visitInsn(Opcodes.IDIV);
									super.visitIntInsn(Opcodes.BIPUSH, 124);
									super.visitInsn(Opcodes.ISUB);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiMainMenu", "height", "I");
									super.visitInsn(Opcodes.ICONST_4);
									super.visitInsn(Opcodes.IDIV);
									super.visitIntInsn(Opcodes.BIPUSH, 48);
									super.visitInsn(Opcodes.IADD);
									super.visitMethodInsn(Opcodes.INVOKESPECIAL, "rs485/secondarymonitor/firstjvm/gui/GuiButtonStartSndJVM", "<init>", "(III)V");
									super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
									super.visitInsn(Opcodes.POP);
								}
								super.visitLabel(label);
							}
						};
				        methods.add(mn);
				        return mn;
					} else if(access == Opcodes.ACC_PROTECTED && name.equals("actionPerformed") && desc.equals("(Lnet/minecraft/client/gui/GuiButton;)V")) { //TODO srgNames
						MethodNode mn = new MethodNode(access, name, desc, signature, exceptions) {
							@Override
							public void visitCode() {
								super.visitCode();
								Label l0 = new Label();
								super.visitLabel(l0);
								super.visitVarInsn(Opcodes.ALOAD, 1);
								super.visitMethodInsn(Opcodes.INVOKESTATIC, "rs485/secondarymonitor/firstjvm/asm/ASMHookClass", "handleMainMenuButton", "(Lnet/minecraft/client/gui/GuiButton;)Z");
								Label l1 = new Label();
								super.visitJumpInsn(Opcodes.IFEQ, l1);
								super.visitInsn(Opcodes.RETURN);
								super.visitLabel(l1);
							}
						};
				        methods.add(mn);
				        return mn;
					} else {
						return super.visitMethod(access, name, desc, signature, exceptions);
					}
				}
				
			};
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else if(name.equals("net.minecraft.client.gui.GuiIngameMenu")) {
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode() {

				@Override
				public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
					if(access == Opcodes.ACC_PUBLIC && name.equals("initGui") && desc.equals("()V")) { //TODO srgNames
						MethodNode mn = new MethodNode(access, name, desc, signature, exceptions) {
							ASMState changing = ASMState.WAITING;
							
							@Override
							public void visitMethodInsn(int opcode, String owner, String name, String desc) {
								if(changing == ASMState.WAITING && opcode == Opcodes.INVOKEINTERFACE && "java/util/List".equals(owner) && "clear".equals(name) && "()V".equals(desc)) {
									changing = ASMState.CHANGING;
								}
								super.visitMethodInsn(opcode, owner, name, desc);
							}

							@Override
							public void visitLabel(Label label) {
								if(changing == ASMState.CHANGING) {
									Label l = new Label();
									super.visitLabel(l);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiIngameMenu", "buttonList", "Ljava/util/List;");
									super.visitTypeInsn(Opcodes.NEW, "rs485/secondarymonitor/firstjvm/gui/GuiButtonStartSndJVM");
									super.visitInsn(Opcodes.DUP);
									super.visitIntInsn(Opcodes.SIPUSH, 1201);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiIngameMenu", "width", "I");
									super.visitInsn(Opcodes.ICONST_2);
									super.visitInsn(Opcodes.IDIV);
									super.visitIntInsn(Opcodes.BIPUSH, 124);
									super.visitInsn(Opcodes.ISUB);
									super.visitVarInsn(Opcodes.ALOAD, 0);
									super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiIngameMenu", "height", "I");
									super.visitInsn(Opcodes.ICONST_4);
									super.visitInsn(Opcodes.IDIV);
									super.visitIntInsn(Opcodes.BIPUSH, 8);
									super.visitInsn(Opcodes.IADD);
									super.visitMethodInsn(Opcodes.INVOKESPECIAL, "rs485/secondarymonitor/firstjvm/gui/GuiButtonStartSndJVM", "<init>", "(III)V");
									super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
									super.visitInsn(Opcodes.POP);
									changing = ASMState.DONE;
								}
								super.visitLabel(label);
							}
						};
				        methods.add(mn);
				        return mn;
					} else if(access == Opcodes.ACC_PROTECTED && name.equals("actionPerformed") && desc.equals("(Lnet/minecraft/client/gui/GuiButton;)V")) { //TODO srgNames
						MethodNode mn = new MethodNode(access, name, desc, signature, exceptions) {
							@Override
							public void visitCode() {
								super.visitCode();
								Label l0 = new Label();
								super.visitLabel(l0);
								super.visitVarInsn(Opcodes.ALOAD, 1);
								super.visitMethodInsn(Opcodes.INVOKESTATIC, "rs485/secondarymonitor/firstjvm/asm/ASMHookClass", "handleIngameMenuButton", "(Lnet/minecraft/client/gui/GuiButton;)Z");
								Label l1 = new Label();
								super.visitJumpInsn(Opcodes.IFEQ, l1);
								super.visitInsn(Opcodes.RETURN);
								super.visitLabel(l1);
							}
						};
				        methods.add(mn);
				        return mn;
					} else {
						return super.visitMethod(access, name, desc, signature, exceptions);
					}
				}
				
			};
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else {
			return bytes;
		}
	}
	
}
