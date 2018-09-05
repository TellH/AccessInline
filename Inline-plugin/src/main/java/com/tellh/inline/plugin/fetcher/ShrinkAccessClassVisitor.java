package com.tellh.inline.plugin.fetcher;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

/**
 * Created by tlh on 2018/8/29.
 */

public class ShrinkAccessClassVisitor extends ClassVisitor {
    private final Context context;
    private String className;

    public ShrinkAccessClassVisitor(ClassVisitor cv, Context context) {
        super(Opcodes.ASM5, cv);
        this.context = context;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (context.isAccessedMember(this.className, name, desc)) {
            access = access & ~Opcodes.ACC_PRIVATE;
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (context.isAccess$Method(this.className, name, desc)) {
            // delete this method.
            return null;
        }
        if (context.isAccessedMember(this.className, name, desc)) {
            access = access & ~Opcodes.ACC_PRIVATE;
        }
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new AccessMethodVisitor(mv);
    }


    class AccessMethodVisitor extends MethodVisitor {

        AccessMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            // TODO: 2018/9/3 super调用时有问题。。。
            if (opcode == Opcodes.INVOKESPECIAL && context.isPrivateAccessedMember(owner, name, desc)) {
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc, itf);
                return;
            }
            Access$MethodEntity access$Method = context.getAccess$Method(owner, name, desc);
            if (access$Method == null) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            List<AbstractInsnNode> insnNodes = access$Method.getInsnNodeList();

            for (AbstractInsnNode insnNode : insnNodes) {
                insnNode.accept(this);
            }
        }
    }
}
