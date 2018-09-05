package com.tellh.inline.plugin.graph;

import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by gengwanpeng on 17/4/26.
 */
public class MetaGraphGeneratorImpl implements MetaGraphGenerator {

    // Key is class name. value is class node.
    private Map<String, Node> nodeMap = new ConcurrentHashMap<>(2 >> 16);
    private Graph graph;

    // thread safe
    public void add(ClassEntity entity) {
        Node current = getOrPutEmpty((entity.access & Opcodes.ACC_INTERFACE) != 0, entity.name);

        ClassNode superNode = null;
        List<InterfaceNode> interfaceNodes = Collections.emptyList();
        if (entity.superName != null) {
            superNode = (ClassNode) getOrPutEmpty(false, entity.superName);
        }
        if (entity.interfaces.size() > 0) {
            interfaceNodes = entity.interfaces.stream().map(i -> (InterfaceNode) getOrPutEmpty(true, i)).collect(Collectors.toList());
        }

        current.entity = entity;
        current.parent = superNode;
        current.interfaces = interfaceNodes;
    }

    // find node by name, if node is not exist then create and add it.
    private Node getOrPutEmpty(boolean isInterface, String className) {
        return nodeMap.computeIfAbsent(className, n -> isInterface ?
                new InterfaceNode(n) :
                new ClassNode(n));
    }


    @Override
    public Graph generate() {
        if (graph == null) {
            graph = new Graph(nodeMap);
            graph.prepare();
        }
        return graph;
    }
}
