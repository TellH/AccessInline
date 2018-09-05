package com.tellh.inline.plugin.graph;

import java.util.Objects;

public abstract class MemberEntity {
    public static final int ACCESS_UNKNOWN = -1;
    protected int access;
    protected String ClassName;
    protected String name;
    protected String desc;

    public MemberEntity(int access, String className, String name, String desc) {
        this.access = access;
        ClassName = className;
        this.name = name;
        this.desc = desc;
    }

    public int access() {
        return access;
    }

    public String className() {
        return ClassName;
    }

    public String name() {
        return name;
    }

    public String desc() {
        return desc;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberEntity that = (MemberEntity) o;
        return Objects.equals(ClassName, that.ClassName) &&
                Objects.equals(name, that.name) &&
                Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ClassName, name, desc);
    }

    @Override
    public String toString() {
        return "MemberEntity{" +
                "access=" + access +
                ", ClassName='" + ClassName + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
