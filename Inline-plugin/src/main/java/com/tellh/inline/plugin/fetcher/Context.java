package com.tellh.inline.plugin.fetcher;

import com.tellh.inline.plugin.graph.ClassEntity;
import com.tellh.inline.plugin.graph.FieldEntity;
import com.tellh.inline.plugin.graph.Graph;
import com.tellh.inline.plugin.graph.MemberEntity;
import com.tellh.inline.plugin.graph.MetaGraphGeneratorImpl;
import com.tellh.inline.plugin.graph.MethodEntity;
import com.tellh.inline.plugin.log.Log;
import com.tellh.inline.plugin.utils.TypeUtil;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Context {
    private AtomicInteger deleteAccess$Count = new AtomicInteger(0);
    private static final String SEPARATOR = "#";

    private static String getKey(String owner, String name, String desc) {
        return owner + SEPARATOR + name + SEPARATOR + desc;
    }

    // key is the access$ method identity, value is the access$ method code.
    private final Map<String, Access$MethodEntity> access$Methods = new ConcurrentHashMap<>(512);

    private final Map<String, MemberEntity> accessedMembers = new ConcurrentHashMap<>(512);

    private MetaGraphGeneratorImpl generator = new MetaGraphGeneratorImpl();

    private Graph graph;

    public Access$MethodEntity addAccess$Method(String owner, String name, String desc) {
        Access$MethodEntity entity = new Access$MethodEntity(owner, name, desc);
        access$Methods.put(getKey(owner, name, desc), entity);
        return entity;
    }

    public MemberEntity addAccessedMembers(String owner, String name, String desc, boolean isField) {
        MemberEntity target;
        if (isField) {
            Log.d(String.format("Found access$ method target field( owner = [%s], name = [%s], desc = [%s] )", owner, name, desc));
            target = new FieldEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
        } else {
            Log.d(String.format("Found access$ method target method( owner = [%s], name = [%s], desc = [%s] )", owner, name, desc));
            target = new MethodEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
        }
        accessedMembers.put(getKey(owner, name, desc), target);
        return target;
    }

    public Access$MethodEntity getAccess$Method(String owner, String name, String desc) {
        return access$Methods.get(getKey(owner, name, desc));
    }

    public void addEntity(ClassEntity entity) {
        generator.add(entity);
    }

    public int methodCount() {
        return access$Methods.size();
    }

    public Graph graph() {
        if (graph == null) {
            graph = generator.generate();
            // TODO: 2018/9/4 需要确认每一个accessedMembers的访问范围
            for (Access$MethodEntity entity : access$Methods.values()) {
                MemberEntity target = entity.getTarget();
                graph.confirmAccess(target)
                        .forEach(m -> accessedMembers.put(getKey(m.className(), m.name(), m.desc()), m));
            }
            for (Map.Entry<String, Access$MethodEntity> entry : access$Methods.entrySet()) {
                Access$MethodEntity entity = entry.getValue();
                MemberEntity target = entity.getTarget();
                if (target.access() == MemberEntity.ACCESS_UNKNOWN) {
                    access$Methods.remove(entry.getKey());
                    accessedMembers.remove(getKey(target.className(), target.name(), target.desc()));
                } else if (TypeUtil.isPrivate(target.access())) {
                    for (AbstractInsnNode insnNode : entity.getInsnNodeList()) {
                        if (insnNode instanceof MethodInsnNode) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;
                            if (methodInsnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                                methodInsnNode.setOpcode(Opcodes.INVOKEVIRTUAL);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return graph;
    }

    public boolean isAccessedMember(String owner, String name, String desc) {
        return accessedMembers.containsKey(getKey(owner, name, desc));
    }

    public boolean isAccess$Method(String owner, String name, String desc) {
        return access$Methods.containsKey(getKey(owner, name, desc));
    }

    public boolean isPrivateAccessedMember(String owner, String name, String desc) {
        MemberEntity entity = accessedMembers.get(getKey(owner, name, desc));
        return entity != null && TypeUtil.isPrivate(entity.access());
    }
}
