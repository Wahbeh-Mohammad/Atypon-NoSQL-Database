package com.atypon.read_controller.cache;

public interface AbstractCache<Key, Value> {
    Value get(Key key);
    void put(Key key, Value value);
}
