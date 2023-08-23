package com.saveunhappy.saitama.compiler.domain.type;

public interface Type {
    String getName();
    Class<?> getTypeClass();
    String getDescriptor();
    String getInternalName();
}
