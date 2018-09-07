package com.tellh.inline.plugin.fetcher;

import com.tellh.inline.plugin.log.Log;
import com.tellh.inline.plugin.utils.TypeUtil;

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
            if (TypeUtil.isPrivate(access)) {
                access = access & ~Opcodes.ACC_PRIVATE;
            } else if (TypeUtil.isProtected(access)) {
                access = access & ~Opcodes.ACC_PROTECTED;
                access = access | Opcodes.ACC_PUBLIC;
            }
            Log.d(String.format("Change Field( className = [%s], methodName = [%s], desc = [%s] ) access, from [%s] to be not private",
                    className, name, desc, String.valueOf(access)));
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (context.isAccess$Method(this.className, name, desc)) {
            // delete this method.
            Log.d(String.format("Access$ method : className = [%s], methodName = [%s], desc = [%s]", className, name, desc));
            return null;
        }
        if (context.isAccessedMember(this.className, name, desc)) {
            if (TypeUtil.isPrivate(access)) {
                access = access & ~Opcodes.ACC_PRIVATE;
            } else if (TypeUtil.isProtected(access)) {
                access = access & ~Opcodes.ACC_PROTECTED;
                access = access | Opcodes.ACC_PUBLIC;
            }
            Log.d(String.format("Change method( className = [%s], methodName = [%s], desc = [%s] ) access, from [%s] to be not private",
                    className, name, desc, String.valueOf(access)));
        }
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        return new AccessMethodVisitor(mv, this.className, name, desc);
    }


    class AccessMethodVisitor extends MethodVisitor {
        private final String owner;
        private final String name;
        private final String desc;

        public AccessMethodVisitor(MethodVisitor mv, String owner, String name, String desc) {
            super(Opcodes.ASM5, mv);
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            // TODO: 2018/9/3 super调用时有问题。。。待check。。。
            if (opcode == Opcodes.INVOKESPECIAL && context.isPrivateAccessedMember(owner, name, desc)) {
                Log.d(String.format("In method( className = [%s], methodName = [%s], desc = [%s] ) code " +
                                ",alter method( className = [%s], methodName = [%s], desc = [%s] ) invoke instruction, from INVOKESPECIAL to INVOKEVIRTUAL",
                        this.owner, this.name, this.desc, owner, name, desc));
                // This method access was changed to be public
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc, itf);
                return;
            }
            Access$MethodEntity access$Method = context.getAccess$Method(owner, name, desc);
            if (access$Method == null) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
                return;
            }
            Log.d(String.format("In method( className = [%s], methodName = [%s], desc = [%s] ) code " +
                            ",inline method( className = [%s], methodName = [%s], desc = [%s] ) invoke",
                    this.owner, this.name, this.desc, owner, name, desc));
            List<AbstractInsnNode> insnNodes = access$Method.getInsnNodeList();

            for (AbstractInsnNode insnNode : insnNodes) {
                insnNode.accept(this);
            }
        }
    }
}
