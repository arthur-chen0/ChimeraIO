/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jht.datamatrixlib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class MultiMap<K, V> {
    private final Map<K, List<V>> mInternalMap;
    public MultiMap() {
        mInternalMap = new ConcurrentHashMap<K, List<V>>();
    }

    public void clear() {
        mInternalMap.clear();
    }

    public boolean containsKey(K key) {
        return mInternalMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        for (List<V> valueList : mInternalMap.values()) {
            if (valueList.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public List<V> get(K key) {
        return mInternalMap.get(key);
    }

    public boolean isEmpty() {
        return mInternalMap.isEmpty();
    }

    public Set<K> keySet() {
        return mInternalMap.keySet();
    }

    public V put(K key, V value) {
        List<V> valueList = mInternalMap.get(key);
        if (valueList == null) {
            valueList = new CopyOnWriteArrayList<>();
            mInternalMap.put(key, valueList);
        }
        valueList.add(value);
        return value;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void putAll(MultiMap<K, ? extends V> m) {
        for (K key : m.keySet()) {
            for (V value : m.get(key)) {
                put(key, value);
            }
        }
    }

    public List<V> remove(K key) {
        return mInternalMap.remove(key);
    }

    public void removeFromList (K key, V value)
    {
        List<V> valueList = mInternalMap.get(key);
        if (valueList != null)
        {
            valueList.remove (value);
        }
    }

    public int size() {
        return mInternalMap.size();
    }

    public List<V> values() {
        List<V> allValues = new CopyOnWriteArrayList<>();
        for (List<V> valueList : mInternalMap.values()) {
            allValues.addAll(valueList);
        }
        return allValues;
    }

    public Map<String, V> getUniqueMap() {
        Map<String, V> uniqueMap = new HashMap<String, V>();
        for (Map.Entry<K, List<V>> entry : mInternalMap.entrySet()) {
            int count = 1;
            for (V value : entry.getValue()) {
                if (count == 1) {
                    addUniqueEntry(uniqueMap, entry.getKey().toString(), value);
                } else {
                    // append unique number to key for each value
                    addUniqueEntry(uniqueMap, String.format("%s%d", entry.getKey(), count), value);
                }
                count++;
            }
        }
        return uniqueMap;
    }

    private String addUniqueEntry(Map<String, V> uniqueMap, String proposedKey, V value) {
        // not the most efficient algorithm, but should work
        if (uniqueMap.containsKey(proposedKey)) {
            return addUniqueEntry(uniqueMap, String.format("%s%s", proposedKey, "X"), value);
        } else {
            uniqueMap.put(proposedKey, value);
            return proposedKey;
        }
    }

    @Override
    public int hashCode() {
        return mInternalMap.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }


        //MultiMap<?, ?> other = (MultiMap<?, ?>) obj;
        return false; //Objects.equals(mInternalMap, other.mInternalMap);
    }
}