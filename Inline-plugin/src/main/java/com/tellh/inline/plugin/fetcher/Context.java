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
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
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

    public synchronized MemberEntity addAccessedMembers(String owner, String name, String desc, boolean isField) {
        String targetKey = getKey(owner, name, desc);
        MemberEntity target = accessedMembers.get(targetKey);
        if (target == null) {
            if (isField) {
                Log.d(String.format("Found access$ method target field( owner = [%s], name = [%s], desc = [%s] )", owner, name, desc));
                target = new FieldEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
            } else {
                Log.d(String.format("Found access$ method target method( owner = [%s], name = [%s], desc = [%s] )", owner, name, desc));
                target = new MethodEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
            }
            accessedMembers.put(targetKey, target);
        } else {
            target.inc();
        }
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
            Map<MemberEntity, List<MemberEntity>> overrideMap = new HashMap<>();
            for (Map.Entry<String, Access$MethodEntity> entry : access$Methods.entrySet()) {
                Access$MethodEntity entity = entry.getValue();
                MemberEntity target = entity.getTarget();
                if (target.access() == MemberEntity.ACCESS_UNKNOWN) {
                    List<MemberEntity> overrideMembers = graph.confirmAccess(target);
                    if (overrideMembers != null && !overrideMembers.isEmpty()) {
                        overrideMap.putIfAbsent(target, overrideMembers);
                    }
                }
                boolean shouldSkip = false;
                String targetKey = getKey(target.className(), target.name(), target.desc());
                if (target.access() == MemberEntity.ACCESS_UNKNOWN) {
                    access$Methods.remove(entry.getKey());
                    accessedMembers.remove(targetKey);
                    Log.d(String.format("Skip inline access to %s (owner = [%s], name = [%s], desc = [%s])",
                            target instanceof FieldEntity ? FieldEntity.class.getSimpleName() : MethodEntity.class.getSimpleName(),
                            target.className(), target.name(), target.desc()));
                    shouldSkip = true;
                } else {
                    MethodInsnNode methodInsn = entity.getMethodInsn();
                    if (methodInsn != null && methodInsn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        if (TypeUtil.isPrivate(target.access())) {
                            methodInsn.setOpcode(Opcodes.INVOKEVIRTUAL);
                        } else {
                            // Skip the super invoke...
                            shouldSkip = true;
                            access$Methods.remove(entry.getKey());
                            target.dec();
                            if (target.isFree()) {
                                accessedMembers.remove(targetKey);
                                Log.d(String.format("Skip inline access to %s (owner = [%s], name = [%s], desc = [%s])",
                                        target instanceof FieldEntity ? FieldEntity.class.getSimpleName() : MethodEntity.class.getSimpleName(),
                                        target.className(), target.name(), target.desc()));
                            }
                        }
                    }
                }
                if (shouldSkip) {
                    Log.d(String.format("Skip inline access$ method (owner = [%s], name = [%s], desc = [%s])",
                            entity.className(), entity.name(), entity.desc()));
                } else {
                    List<MemberEntity> overrideMembers = overrideMap.get(target);
                    if (overrideMembers != null) {
                        overrideMembers.forEach(m -> {
                            String key = getKey(m.className(), m.name(), m.desc());
                            MemberEntity existMember = accessedMembers.get(key);
                            if (existMember == null) {
                                accessedMembers.put(key, m);
                            } else {
                                existMember.inc();
                            }
                        });
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
