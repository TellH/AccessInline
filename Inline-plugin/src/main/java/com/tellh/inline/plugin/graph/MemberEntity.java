package com.tellh.inline.plugin.graph;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MemberEntity {
    public static final int ACCESS_UNKNOWN = -1;
    protected int access;
    protected String ClassName;
    protected String name;
    protected String desc;
    private final AtomicInteger referenceCount;

    public MemberEntity(int access, String className, String name, String desc) {
        this.access = access;
        ClassName = className;
        this.name = name;
        this.desc = desc;
        referenceCount = new AtomicInteger(1);
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

    public void inc() {
        referenceCount.getAndIncrement();
    }

    public void dec() {
        referenceCount.getAndDecrement();
    }

    public boolean isFree() {
        return referenceCount.get() <= 0;
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
