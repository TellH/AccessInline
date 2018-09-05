package com.tellh.inline.plugin.graph;

import com.tellh.inline.plugin.utils.TypeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by gengwanpeng on 17/5/5.
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

    public void confirmAccess(MemberEntity target) {
        Node node = get(target.ClassName);
        while (node != null) {
            ClassEntity classEntity = node.entity;
            if (target instanceof MethodEntity) {
                for (MethodEntity method : classEntity.methods) {
                    if (target.name().equals(method.name()) && target.desc().equals(method.desc())) {
                        target.setAccess(method.access());
                        return;
                    }
                }
            } else if (target instanceof FieldEntity) {
                for (FieldEntity field : classEntity.fields) {
                    if (target.name().equals(field.name()) && target.desc().equals(field.desc())) {
                        target.setAccess(field.access());
                        return;
                    }
                }
            }
            node = node.parent;
        }
    }
}
