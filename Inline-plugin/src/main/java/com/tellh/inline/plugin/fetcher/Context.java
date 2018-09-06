package com.tellh.inline.plugin.fetcher;

import com.tellh.inline.plugin.graph.ClassEntity;
import com.tellh.inline.plugin.graph.FieldEntity;
import com.tellh.inline.plugin.graph.Graph;
import com.tellh.inline.plugin.graph.MemberEntity;
import com.tellh.inline.plugin.graph.MetaGraphGeneratorImpl;
import com.tellh.inline.plugin.graph.MethodEntity;
import com.tellh.inline.plugin.log.Log;
import com.tellh.inline.plugin.utils.TypeUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Context {
    private static final String SEPARATOR = "#";

    private static String getKey(String owner, String name, String desc) {
        return owner + SEPARATOR + name + SEPARATOR + desc;
    }

    // key is the access$ method identity, value is the access$ method code.
    private final Map<String, Access$MethodEntity> access$Methods = new ConcurrentHashMap<>(512);

    private final Set<String> accessedMembers = ConcurrentHashMap.newKeySet(512);

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
            target = new FieldEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
        } else {
            target = new MethodEntity(MemberEntity.ACCESS_UNKNOWN, owner, name, desc);
        }
        accessedMembers.add(getKey(owner, name, desc));
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
            int skipCount = 0;
            // TODO: 2018/9/4 需要确认每一个accessedMembers的访问范围
            for (Access$MethodEntity entity : access$Methods.values()) {
                MemberEntity target = entity.getTarget();
                graph.confirmAccess(target);
                // TODO: 2018/9/4 protected的非static成员暂时不内联了。。。
                if (TypeUtil.isProtected(target.access()) && !TypeUtil.isStatic(target.access())) {
                    target.setAccess(MemberEntity.ACCESS_UNKNOWN);
                    skipCount++;
                }
            }
            Log.i("Skip Access$ inline count = " + skipCount);
            access$Methods.values().removeIf(entity -> {
                MemberEntity target = entity.getTarget();
                if (target.access() == MemberEntity.ACCESS_UNKNOWN) {
                    accessedMembers.remove(getKey(target.className(), target.name(), target.desc()));
                    return true;
                } else {
                    return false;
                }
            });
        }
        return graph;
    }

    public boolean isAccessedMember(String owner, String name, String desc) {
        return accessedMembers.contains(getKey(owner, name, desc));
    }

    public boolean isAccess$Method(String owner, String name, String desc) {
        return access$Methods.containsKey(getKey(owner, name, desc));
    }

    public boolean isPrivateAccessedMember(String owner, String name, String desc) {
        Access$MethodEntity entity = access$Methods.get(getKey(owner, name, desc));
        return entity != null && TypeUtil.isPrivate(entity.access());
    }
}
