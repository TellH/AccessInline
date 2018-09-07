package com.tellh.inline.plugin.graph;

import java.util.Collections;
import java.util.List;

public class ClassNode extends Node {

    public List<ClassNode> children = Collections.emptyList();

    public ClassNode(String className) {
        super(new ClassEntity(className), null, Collections.emptyList());
    }
}
