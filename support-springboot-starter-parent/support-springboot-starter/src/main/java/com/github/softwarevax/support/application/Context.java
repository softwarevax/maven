package com.github.softwarevax.support.application;

import java.util.*;

public class Context {

    private Map<String, Object> parameters;

    public Context() {
        this.parameters = new HashMap<>();
    }

    public Context(Map<String, Object> parameters) {
        this();
        if(parameters != null) {
            this.parameters = parameters;
        }
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        if(parameters.size() > 0) {
            parameters.clear();
        }
    }

    /**
     * Gets value mapped to key, returning null if unmapped.
     * @param key
     * @return
     */
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     * @param key
     * @param defaultValue
     * @return
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        if(this.parameters.containsKey(key)) {
            return "true".equals(this.parameters.get(key)) ? true : false;
        }
        return defaultValue;
    }

    /**
     * Gets value mapped to key, returning null if unmapped.
     * @param key
     * @return
     */
    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     * @param key
     * @param defaultValue
     * @return
     */
    public Integer getInteger(String key, Integer defaultValue) {
        if(this.parameters.containsKey(key)) {
            try {
                return (Integer) this.parameters.get(key);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Gets value mapped to key, returning null if unmapped.
     * @param key
     * @return
     */
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     * @param key
     * @param defaultValue
     * @return
     */
    public Long getLong(String key, Long defaultValue) {
        if(this.parameters.containsKey(key)) {
            try {
                return (Long) this.parameters.get(key);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    public <T> T get(String key) {
        return (T) parameters.get(key);
    }

    /**
     *  return parameters
     * @return
     */
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        if(this.parameters.containsKey(key)) {
            return null == this.parameters.get(key) ? defaultValue : (String) this.parameters.get(key);
        }
        return defaultValue;
    }

    /**
     * put a element to paramters
     * @param key
     * @param value
     * @param override
     */
    public void put(String key, Object value, boolean override) {
        if(override) {
            this.parameters.put(key,value);
        }
    }

    /**
     * put a element to paramters, override element
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        put(key, value, true);
    }

    /**
     * put some parameters
     * @param map
     */
    public void putAll(Map<String, Object> map) {
        this.parameters.putAll(map);
    }

    public List<Object> getPrefix(String ... prefix) {
        List<Object> prefixObjs = new ArrayList<>();
        Iterator<Map.Entry<String, Object>> iterator = this.parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            for(String pre : prefix) {
                if(pre == null || pre.equals("")) {
                    continue;
                }
                if(key.startsWith(pre)) {
                    prefixObjs.add(entry.getValue());
                }
            }
        }
        return prefixObjs;
    }
    /**
     * toString
     * @return
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<Map.Entry<String, Object>> iterator = this.parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }
}
