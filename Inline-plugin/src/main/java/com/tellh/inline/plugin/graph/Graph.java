package com.tellh.inline.plugin.graph;

import com.tellh.inline.plugin.utils.TypeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Class dependency graph.
 */
public class Graph {


    private final Map<String, Node> nodeMap;

    public Graph(Map<String, Node> nodesMap) {
        this.nodeMap = nodesMap;
    }

    /**
     * Before prepare, the Graph only has vector from child to super.
     * this method will add vector from super to child.
     * After prepare, there is a full graph.
     */
    public void prepare() {
        nodeMap.values()
                .forEach(n -> {
                    if (n.parent != null) {
                        ClassNode parent = n.parent;
                        if (parent.children == Collections.EMPTY_LIST) {

                            // optimize for Object
                            if (parent.entity.name.equals("java/lang/Object")) {
                                parent.children = new ArrayList<>(nodeMap.size() >> 1);
                            } else {
                                parent.children = new ArrayList<>();
                            }
                        }
                        // all interfaces extends java.lang.Object
                        // make java.lang.Object subclasses purely
                        if (n instanceof ClassNode) {
                            parent.children.add((ClassNode) n);
                        }
                    }
                    n.interfaces.forEach(i -> {
                        if (n instanceof InterfaceNode) {
                            if (i.children == Collections.EMPTY_LIST) {
                                i.children = new ArrayList<>();
                            }
                            i.children.add((InterfaceNode) n);
                        } else {
                            if (i.implementedClasses == Collections.EMPTY_LIST) {
                                i.implementedClasses = new ArrayList<>();
                            }
                            //noinspection ConstantConditions
                            i.implementedClasses.add((ClassNode) n);
                        }
                    });
                });
    }

    public boolean inherit(String child, String parent) {
        Node node = nodeMap.get(child);
        while (node != null && !parent.equals(node.entity.name)) {
            node = node.parent;
        }
        return node != null;
    }


    public Node get(String className) {
        return nodeMap.get(className);
    }

    public List<MemberEntity> confirmAccess(MemberEntity target) {
        List<MemberEntity> accessShouldBeChangeList = new ArrayList<>();
        Node node = get(target.ClassName);
        // backtrace to super
        while (node != null) {
            ClassEntity classEntity = node.entity;
            List<? extends MemberEntity> members = target instanceof MethodEntity ? classEntity.methods : classEntity.fields;
            for (MemberEntity m : members) {
                if (target.name().equals(m.name()) && target.desc().equals(m.desc())) {
                    // found it!
                    // if the class in android.jar, there is no way to change the access.
                    if (!classEntity.fromAndroid) {
                        target.setAccess(m.access());
                        accessShouldBeChangeList.add(m);
                        // forwards to children to find override methods
                        if (m instanceof MethodEntity && !TypeUtil.isStatic(m.access())) {
                            accessShouldBeChangeList.addAll(collectOverrideMethods(m, accessShouldBeChangeList));
                        }
                    }
                    if (TypeUtil.isPublic(m.access())) {
                        target.setAccess(m.access());
                    }
                    return accessShouldBeChangeList;
                }
            }
            node = node.parent;
        }
        return accessShouldBeChangeList;
    }

    public List<MemberEntity> collectOverrideMethods(MemberEntity target, List<MemberEntity> list) {
        ClassNode classNode = (ClassNode) get(target.ClassName);
        // bfs
        Queue<ClassNode> handleQ = new LinkedList<>();
        classNode.children.forEach(handleQ::offer);
        while (!handleQ.isEmpty()) {
            ClassNode node = handleQ.poll();
            ClassEntity classEntity = node.entity;
            for (MethodEntity m : classEntity.methods) {
                if (m.name().equals(target.name()) && m.desc().equals(target.desc())) {
                    list.add(m);
                    break;
                }
            }
            node.children.forEach(handleQ::offer);
        }
        return list;
    }
}
