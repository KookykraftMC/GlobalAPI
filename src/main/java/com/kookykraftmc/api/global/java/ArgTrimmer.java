package com.kookykraftmc.api.global.java;

import java.lang.reflect.Array;

public class ArgTrimmer<T> {

    private T[] original;
    private Class<T> clazz;

    public ArgTrimmer(Class<T> clazz, T[] original) {
        this.original = original;
        this.clazz = clazz;
    }

    public T[] trim(int i) {
        if (i < 0 || i > original.length) {
            throw new IllegalArgumentException("Invalid trim length");
        }
        int length = original.length - i;
        if (length <= 0) {
            return newInstance(0);
        }
        T[] newargs = newInstance(length);
        System.arraycopy(original, i, newargs, 0, length);
        return newargs;
    }

    private T[] newInstance(int i) {
        return (T[]) Array.newInstance(clazz, i);
    }

}
