package com.tellh.inline.plugin.fetcher;

import com.tellh.inline.plugin.graph.ClassEntity;
import com.tellh.inline.plugin.graph.FieldEntity;
import com.tellh.inline.plugin.graph.MethodEntity;
import com.tellh.inline.plugin.utils.TypeUtil;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by gengwanpeng on 17/4/27.
 */
public class PreProcessClassVisitor extends ClassVisitor {

    private ClassEntity entity;
    private Context context;
    private boolean fromAndroidSDK;

    PreProcessClassVisitor(Context context) {
        this(context, false);
    }

    PreProcessClassVisitor(Context context, boolean fromAndroidSDK) {
        super(Opcodes.ASM5, null);
        this.context = context;
        this.fromAndroidSDK = fromAndroidSDK;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        entity = new ClassEntity(access, name, superName, interfaces == null ? Collections.emptyList() : Arrays.asList(interfaces));
        entity.fromAndroid = fromAndroidSDK;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        entity.fields.add(new FieldEntity(access, entity.name, name, desc));
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        entity.methods.add(new MethodEntity(access, entity.name, name, desc));
        if (!fromAndroidSDK && TypeUtil.isSynthetic(access) && name.startsWith("access$")) {
            Access$MethodEntity access$MethodEntity = context.addAccess$Method(entity.name, name, desc);
            return new RefineAccess$MethodVisitor(context, access$MethodEntity);
        }
        return null;
    }

    public ClassEntity getEntity() {
        return entity;
    }

    static class RefineAccess$MethodVisitor extends MethodVisitor {

        private List<AbstractInsnNode> refinedInsns;
        private Access$MethodEntity access$MethodEntity;
        private Context context;

        public RefineAccess$MethodVisitor(Context context, Access$MethodEntity access$MethodEntity) {
            super(Opcodes.ASM5);
            this.refinedInsns = new ArrayList<>();
            access$MethodEntity.setInsnNodeList(refinedInsns);
            this.access$MethodEntity = access$MethodEntity;
            this.context = context;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            refinedInsns.add(new FieldInsnNode(opcode, owner, name, desc));
            access$MethodEntity.setTarget(context.addAccessedMembers(owner, name, desc, true));
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
//            if (opcode == Opcodes.INVOKESPECIAL) {
//                opcode = Opcodes.INVOKEVIRTUAL;
//            }
            refinedInsns.add(new MethodInsnNode(opcode, owner, name, desc, itf));
            access$MethodEntity.setTarget(context.addAccessedMembers(owner, name, desc, false));
        }

        @Override
        public void visitInsn(final int opcode) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                return;
            }
            refinedInsns.add(new InsnNode(opcode));
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String desc, Handle bsm,
                                           Object... bsmArgs) {
            refinedInsns.add(new InvokeDynamicInsnNode(name, desc, bsm, bsmArgs));
        }
    }
}
