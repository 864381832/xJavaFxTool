package com.xwintop.xcore.util;

import java.util.Objects;

/**
 * 键值对，默认是可修改的，可以通过 {@link #unmodifiable()} 方法获取不可修改的键值对。
 */
public class KeyValue<K, V> {

    private K key;

    private V value;

    public KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public KeyValue(KeyValue<K, V> keyValue) {
        this(keyValue.key, keyValue.value);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    /**
     * 创建一个只读实例
     */
    public KeyValue<K, V> unmodifiable() {
        return new KeyValue<K, V>(this) {
            @Override
            public void setKey(K key) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setValue(V value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        KeyValue<?, ?> keyValue = (KeyValue<?, ?>) object;
        return Objects.equals(value, keyValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
