package com.tellh.inline.plugin.fetcher;

import com.tellh.inline.plugin.graph.MemberEntity;
import com.tellh.inline.plugin.graph.MethodEntity;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.List;

public class Access$MethodEntity extends MethodEntity {
    private MemberEntity target;
    private List<AbstractInsnNode> insnNodeList;

    public Access$MethodEntity(String className, String name, String desc) {
        super(Opcodes.ACC_STATIC, className, name, desc);
    }

    public MemberEntity getTarget() {
        return target;
    }

    public void setTarget(MemberEntity target) {
        this.target = target;
    }

    public List<AbstractInsnNode> getInsnNodeList() {
        return insnNodeList;
    }

    public void setInsnNodeList(List<AbstractInsnNode> insnNodeList) {
        this.insnNodeList = insnNodeList;
    }

    public MethodInsnNode getMethodInsn() {
        for (AbstractInsnNode insnNode : insnNodeList) {
            if (insnNode instanceof MethodInsnNode) {
                return (MethodInsnNode) insnNode;
            }
        }
        return null;
    }
}
