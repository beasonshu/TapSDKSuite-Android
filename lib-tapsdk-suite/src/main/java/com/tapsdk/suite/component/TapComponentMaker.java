package com.tapsdk.suite.component;

public class TapComponentMaker<T> {

    private final Class<T> kind;

    public TapComponentMaker(Class<T> kind) {
        this.kind = kind;
    }

    T create() throws InstantiationException, IllegalAccessException {
        return kind.newInstance();
    }
}
