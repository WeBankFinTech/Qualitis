package com.webank.wedatasphere.qualitis.util.map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author v_gaojiedeng
 */
public class CustomObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 7112167235632741571L;

    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String LEFT_SQUARE_BRACKETS = "{";
    private static final String RIGHT_SQUARE_BRACKETS = "}";
    private static final String JOINT_MARK = "\"";

    private static CustomObjectMapper defaultInstance;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    protected static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(CustomObjectMapper.DATE_PATTERN);
        }
    };

    protected static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(CustomObjectMapper.DATETIME_PATTERN);
        }
    };

    protected static final ThreadLocal<SimpleDateFormat> TIMESTAMP_FORMAT_THREAD_LOCAL = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(CustomObjectMapper.TIMESTAMP_PATTERN);
        }
    };

    public static CustomObjectMapper newInstance() {
        return new CustomObjectMapper();
    }

    private static CustomObjectMapper getDefaultInstance() {
        if (CustomObjectMapper.defaultInstance == null) {
            CustomObjectMapper.defaultInstance = new CustomObjectMapper();
        }
        return CustomObjectMapper.defaultInstance;
    }

    public <T> T doTransJsonToObject(String json, T object) {
        if (object == null) {
            throw new IllegalArgumentException("'object' must be not null.");
        }

        try {
            if (Map.class.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("The target object does not support map types.");
            } else if (Collection.class.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("The target object does not support collection types.");
            } else {
                JavaType javaType = this.getTypeFactory().constructType(object.getClass());
                return this.<T>readValue(json, javaType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object, json=" + json + ", object.getClass()=" + object.getClass(), e);
        }
    }

    public <T> T doTransJsonToObject(String json, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' must be not null.");
        }

        try {
            JavaType javaType = this.getTypeFactory().constructType(clazz);
            return this.<T>readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object, json=" + json + ", clazz=" + clazz, e);
        }
    }

    public <T> T doTransJsonToObject(String json, JavaType javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("'javaType' must be not null.");
        }

        try {
            return this.<T>readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object, json=" + json + ", javaType=" + javaType, e);
        }
    }

    public <T> List<T> doTransJsonToObjects(String json, T object) {
        if (object == null) {
            throw new IllegalArgumentException("'object' must be not null.");
        }

        try {
            if (Map.class.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("The target object does not support map types.");
            } else if (Collection.class.isAssignableFrom(object.getClass())) {
                throw new IllegalArgumentException("The target object does not support collection types.");
            } else {
                JavaType elementJavaType = this.getTypeFactory().constructType(object.getClass());
                CollectionType collectionType = CollectionType.construct(List.class, elementJavaType);
                return this.<List<T>>readValue(json, collectionType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object list, json=" + json + ", object.getClass()=" + object.getClass(), e);
        }
    }

    public <T> List<T> doTransJsonToObjects(String json, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' must be not null.");
        }

        try {
            JavaType elementJavaType = this.getTypeFactory().constructType(clazz);
            CollectionType collectionType = CollectionType.construct(List.class, elementJavaType);
            return this.<List<T>>readValue(json, collectionType);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object list, json=" + json + ", clazz=" + clazz, e);
        }
    }

    public <T> List<T> doTransJsonToObjects(String json, JavaType javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("'javaType' must be not null.");
        }

        try {
            CollectionType collectionType = CollectionType.construct(List.class, javaType);
            return this.<List<T>>readValue(json, collectionType);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to object list, json=" + json + ", javaType=" + javaType, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <K, E> Map<K, E> doTransJsonToMap(String json, Class<K> keyClazz, Class<E> elementClazz) {
        if (keyClazz == null) {
            throw new IllegalArgumentException("'keyClazz' must be not null.");
        }
        if (elementClazz == null) {
            throw new IllegalArgumentException("'elementClazz' must be not null.");
        }

        if (json != null) {
            if (json.trim().startsWith(LEFT_BRACKET) && json.trim().endsWith(RIGHT_BRACKET)) {
                Map<K, E> map = new LinkedHashMap<K, E>();
                List<K> list = this.doTransJsonToObjects(json, keyClazz);
                for (K t : list) {
                    if (keyClazz.equals(elementClazz)) {
                        map.put(t, (E) t);
                    } else {
                        String s = this.doTransObjectToJson(t);
                        if (String.class.isAssignableFrom(elementClazz)) {
                            if (s.startsWith("\"") && s.endsWith("\"")) {
                                s = s.substring(1, s.length() - 1);
                            }
                            map.put(t, (E) s);
                        } else {
                            E e = this.doTransJsonToObject(s, elementClazz);
                            map.put(t, e);
                        }
                    }
                }
                return map;
            } else if (!json.trim().startsWith(LEFT_SQUARE_BRACKETS) && !json.trim().endsWith(RIGHT_SQUARE_BRACKETS)) {
                Map<K, E> map = new LinkedHashMap<K, E>();
                String separator = json.contains(";") ? ";" : ",";
                boolean isMap = json.contains("=");
                String[] array = json.split(separator);
                for (String t : array) {
                    K key = null;
                    E element = null;
                    String k = null;
                    String e = null;
                    if (isMap) {
                        k = t.substring(0, json.indexOf("="));
                        e = t.substring(k.length() + 1);
                    } else {
                        k = t;
                        e = t;
                    }

                    if (String.class.isAssignableFrom(keyClazz)) {
                        key = (K) k;
                    } else {
                        key = this.doTransJsonToObject(k, keyClazz);
                    }
                    if (String.class.isAssignableFrom(elementClazz)) {
                        element = (E) e;
                    } else {
                        element = this.doTransJsonToObject(e, elementClazz);
                    }
                    map.put(key, element);
                }
                return map;
            }
        }
        try {
            JavaType mapKeyJavaType = this.getTypeFactory().constructType(keyClazz);
            JavaType mapValueJavaType = this.getTypeFactory().constructType(elementClazz);
            MapType mapType = MapType.construct(LinkedHashMap.class, mapKeyJavaType, mapValueJavaType);
            return this.<Map<K, E>>readValue(json, mapType);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans json to map, json=" + json + ", keyClazz=" + keyClazz + ", elementClazz=" + elementClazz, e);
        }
    }

    public String doTransObjectToJson(Object object) {
        try {
            return this.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans object to json, object=" + object, e);
        }
    }

    public <T> T doTransMapToObject(Map<String, Object> map, T object) {
        if (object == null) {
            throw new IllegalArgumentException("'object' must be not null.");
        }

        try {
            String jsonContent = this.writeValueAsString(map);
            JavaType javaType = this.getTypeFactory().constructType(object.getClass());
            T entity = this.<T>readValue(jsonContent, javaType);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans map to object, map=" + map + ", object.getClass()=" + object.getClass(), e);
        }
    }

    public <T> T doTransMapToObject(Map<String, Object> map, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' must be not null.");
        }

        try {
            String jsonContent = this.writeValueAsString(map);
            T entity = this.<T>readValue(jsonContent, clazz);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans map to object, map=" + map + ", clazz=" + clazz, e);
        }
    }

    public <T> T doTransMapToObject(Map<String, Object> map, JavaType javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("'javaType' must be not null.");
        }

        try {
            String jsonContent = this.writeValueAsString(map);
            T entity = this.<T>readValue(jsonContent, javaType);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans map to object, map=" + map + ", javaType=" + javaType, e);
        }
    }

    public <T> List<T> doTransMapsToObjects(List<Map<String, Object>> maps, T object) {
        if (object == null) {
            throw new IllegalArgumentException("'object' must be not null.");
        }

        List<T> entities = new ArrayList<T>();
        for (Map<String, Object> map : maps) {
            T entity = this.doTransMapToObject(map, object);
            entities.add(entity);
        }
        return entities;
    }

    public <T> List<T> doTransMapsToObjects(List<Map<String, Object>> maps, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("'clazz' must be not null.");
        }

        List<T> objects = new ArrayList<T>();
        for (Map<String, Object> map : maps) {
            T object = this.doTransMapToObject(map, clazz);
            objects.add(object);
        }
        return objects;
    }

    public <T> List<T> doTransMapsToObjects(List<Map<String, Object>> maps, JavaType javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("'javaType' must be not null.");
        }

        List<T> objects = new ArrayList<T>();
        for (Map<String, Object> map : maps) {
            T object = this.doTransMapToObject(map, javaType);
            objects.add(object);
        }
        return objects;
    }

    public Map<String, Object> doTransObjectToMap(Object object) {
        try {
            String jsonContent = this.writeValueAsString(object);
            JavaType mapKeyJavaType = this.getTypeFactory().constructType(String.class);
            JavaType mapValueJavaType = this.getTypeFactory().constructType(Object.class);
            MapType mapType = MapType.construct(LinkedHashMap.class, mapKeyJavaType, mapValueJavaType);
            Map<String, Object> map = this.<Map<String, Object>>readValue(jsonContent, mapType);
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Cannot trans object to map, object=" + object, e);
        }
    }

    public List<Map<String, Object>> doTransObjectsToMaps(List<?> objects) {
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (Object object : objects) {
            Map<String, Object> map = this.doTransObjectToMap(object);
            maps.add(map);
        }
        return maps;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> doTransObjects(List<T> objects, String[] properties) {
        if (objects == null || objects.size() == 0) {
            throw new IllegalArgumentException("'objects' must be not empty.");
        }

        List<Map<String, Object>> maps = this.doTransObjectsToMaps(objects);
        List<Map<String, Object>> newMaps = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> map : maps) {
            Map<String, Object> newMap = new LinkedHashMap<String, Object>();
            for (String property : properties) {
                newMap.put(property, map.get(property));
            }
            newMaps.add(newMap);
        }
        return (List<T>) this.doTransMapsToObjects(newMaps, objects.iterator().next().getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> T doTransObject(T object, String[] properties) {
        if (object == null) {
            throw new IllegalArgumentException("'objects' must be not null.");
        }
        if (properties == null || properties.length == 0) {
            throw new IllegalArgumentException("'properties' must be not empty.");
        }

        Map<String, Object> map = this.doTransObjectToMap(object);
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        for (String property : properties) {
            newMap.put(property, map.get(property));
        }
        return (T) this.doTransMapToObject(newMap, object.getClass());
    }

    public static <T> T transJsonToObject(String json, T object) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObject(json, object);
    }

    public static <T> T transJsonToObject(String json, Class<T> clazz) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObject(json, clazz);
    }

    public static <T> T transJsonToObject(String json, JavaType javaType) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObject(json, javaType);
    }

    public static <K, E> Map<K, E> transJsonToMap(String json, Class<K> keyClazz, Class<E> elementClazz) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToMap(json, keyClazz, elementClazz);
    }

    public static <T> List<T> transJsonToObjects(String json, T object) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObjects(json, object);
    }

    public static <T> List<T> transJsonToObjects(String json, Class<T> clazz) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObjects(json, clazz);
    }

    public static <T> List<T> transJsonToObjects(String json, JavaType javaType) {
        return CustomObjectMapper.getDefaultInstance().doTransJsonToObjects(json, javaType);
    }

    public static String transObjectToJson(Object object) {
        return CustomObjectMapper.getDefaultInstance().doTransObjectToJson(object);
    }

    public static <T> T transMapToObject(Map<String, Object> map, Class<T> clazz) {
        return CustomObjectMapper.getDefaultInstance().doTransMapToObject(map, clazz);
    }

    public static <T> T transMapToObject(Map<String, Object> map, JavaType javaType) {
        return CustomObjectMapper.getDefaultInstance().doTransMapToObject(map, javaType);
    }

    public static <T> List<T> transMapsToObjects(List<Map<String, Object>> maps, T object) {
        return CustomObjectMapper.getDefaultInstance().doTransMapsToObjects(maps, object);
    }

    public static <T> List<T> transMapsToObjects(List<Map<String, Object>> maps, Class<T> clazz) {
        return CustomObjectMapper.getDefaultInstance().doTransMapsToObjects(maps, clazz);
    }

    public static <T> List<T> transMapsToObjects(List<Map<String, Object>> maps, JavaType javaType) {
        return CustomObjectMapper.getDefaultInstance().doTransMapsToObjects(maps, javaType);
    }

    public static Map<String, Object> transObjectToMap(Object object) {
        return CustomObjectMapper.getDefaultInstance().doTransObjectToMap(object);
    }

    public static List<Map<String, Object>> transObjectsToMaps(List<?> objects) {
        return CustomObjectMapper.getDefaultInstance().doTransObjectsToMaps(objects);
    }

    public static <T> List<T> transObjects(List<T> objects, String[] properties) {
        return CustomObjectMapper.getDefaultInstance().doTransObjects(objects, properties);
    }

    public static <T> T transObject(T object, String[] properties) {
        return CustomObjectMapper.getDefaultInstance().doTransObject(object, properties);
    }

    public static String format(String format, Object... values) {
        Object[] args = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                args[i] = "NULL";
            } else {
                Class<?> valClazz = values[i].getClass();
                if (!String.class.isAssignableFrom(valClazz)
                        && !Boolean.class.isAssignableFrom(valClazz)
                        && !boolean.class.isAssignableFrom(valClazz)
                        && !Number.class.isAssignableFrom(valClazz)
                        && !int.class.isAssignableFrom(valClazz)
                        && !long.class.isAssignableFrom(valClazz)
                        && !float.class.isAssignableFrom(valClazz)
                        && !short.class.isAssignableFrom(valClazz)) {
                    String valStr = CustomObjectMapper.transObjectToJson(values[i]);
                    if (valStr.startsWith("\"") && valStr.endsWith("\"")) {
                        args[i] = valStr.substring(1, valStr.length() - 1);
                    }
                    args[i] = valStr;
                } else {
                    args[i] = values[i];
                }
            }
        }
        return String.format(format, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T transValue(Object value, Class<T> clazz) {
        if (value == null) {
            return null;
        }

        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (String.class.isAssignableFrom(value.getClass())) {
            if (Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) {
                return CustomObjectMapper.transJsonToObject((String) value, clazz);
            } else {
                try {
                    return CustomObjectMapper.transJsonToObject("\"" + value + "\"", clazz);
                } catch (Exception e) {
                    try {
                        return CustomObjectMapper.transJsonToObject((String) value, clazz);
                    } catch (Exception ex) {
                        throw new RuntimeException("Cannot trans value, value=" + value + ", clazz=" + clazz, e);
                    }
                }
            }
        } else if (String.class.isAssignableFrom(clazz)) {
            String json = CustomObjectMapper.transObjectToJson(value);
            if (json.startsWith(JOINT_MARK) && json.endsWith(JOINT_MARK)) {
                return (T) json.substring(1, json.length() - 1);
            } else {
                return (T) json;
            }
        } else {
            String json = CustomObjectMapper.transObjectToJson(value);
            return CustomObjectMapper.transJsonToObject(json, clazz);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> transValues(Object values, Class<T> clazz) {
        if (values == null) {
            return Collections.emptyList();
        }

        Collection<Object> temps = new ArrayList<>();
        if (Collection.class.isAssignableFrom(values.getClass())) {
            temps.addAll((Collection) values);
        } else if (values.getClass().isArray()) {
            temps.addAll(Arrays.asList((Object[]) values));
        } else {
            temps.add(values);
        }

        List<T> newValues = new ArrayList<T>();
        for (Object temp : temps) {
            T newValue = CustomObjectMapper.transValue(temp, clazz);
            newValues.add(newValue);
        }
        return newValues;
    }

    public static <T> Map<String, T> transMapValues(Map<String, Object> map, Class<T> valueClazz) {
        if (map == null) {
            return Collections.emptyMap();
        }

        Map<String, T> newMap = new LinkedHashMap<String, T>();
        for (Entry<String, Object> entry : map.entrySet()) {
            T newValue = CustomObjectMapper.transValue(entry.getValue(), valueClazz);
            newMap.put(entry.getKey(), newValue);
        }
        return newMap;
    }
}