package dev.hamze.nanoexchange.common.utility;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@SuppressWarnings("unused")
public final class ReflectionUtil {

    private static final Map<Class<?>, Map<String, List<Annotation>>> ANNOTATIONS_BY_CLASS_FIELD_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Field>> CLASS_FIELD_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<String>> CLASS_FIELD_NAME_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Annotation>> ANNOTATIONS_BY_CLASS_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <I> Class<I> getGenericParameterType(Object object, int parameterIndex) {

        Class<?> type = object.getClass();
        Type[] typeArguments;
        while (true) {
            Type genericSuperClass = type.getGenericSuperclass();
            assert genericSuperClass != null;
            if (genericSuperClass instanceof ParameterizedType) {
                typeArguments = ((ParameterizedType) genericSuperClass).getActualTypeArguments();
                break;
            }

            type = type.getSuperclass();
        }

        Type genericParameterType;
        if (typeArguments.length > parameterIndex) {
            genericParameterType = typeArguments[parameterIndex];
        } else {
            throw new IllegalArgumentException(
                    String.format("Generic parameter with index [%d] doesn't exist", parameterIndex));
        }

        if (genericParameterType instanceof ParameterizedType) {
            return (Class<I>) ((ParameterizedType) genericParameterType).getRawType();
        } else if (genericParameterType instanceof Class) {
            return (Class<I>) genericParameterType;
        } else {
            throw new IllegalArgumentException("Bad input object");
        }
    }

    @SuppressWarnings("unchecked")
    public static <I> Class<I> getGenericParameterType(Object object, Class<?> targetType, int parameterIndex) {

        Class<?> type = object.getClass();
        Type[] typeArguments;
        Deque<Class<?>> typeHierarchyStack = new ArrayDeque<>();
        while (true) {
            typeHierarchyStack.add(type);
            Type genericSuperClass = type.getGenericSuperclass();
            assert genericSuperClass != null;
            if (genericSuperClass instanceof ParameterizedType && targetType.equals(type.getSuperclass())) {
                typeArguments = ((ParameterizedType) genericSuperClass).getActualTypeArguments();
                break;
            }

            type = type.getSuperclass();
        }

        Type genericParameterType;
        if (typeArguments.length > parameterIndex) {
            genericParameterType = typeArguments[parameterIndex];
        } else {
            throw new IllegalArgumentException(
                    String.format("Generic parameter with index [%d] doesn't exist", parameterIndex));
        }

        switch (genericParameterType) {
            case ParameterizedType parameterizedType -> {
                return (Class<I>) parameterizedType.getRawType();
            }
            case Class<?> aClass -> {
                return (Class<I>) genericParameterType;
            }
            case TypeVariable<?> typeVariable -> {
                Class<?> subclass = typeHierarchyStack.removeLast();
                return getGenericParameterType(object, subclass, parameterIndex);
            }
            case null, default -> throw new IllegalArgumentException("Bad input object");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(String fieldName, Object obj) {

        try {
            return (T) PropertyUtils.getProperty(obj, fieldName);
        } catch (Exception ex) {
            if (log.isInfoEnabled()) {
                log.info(fieldName, ex);
            }

            return null;
        }
    }

    public static Object getValue(Class<?> clazz, String fieldName, Object object) {

        if (object == null || fieldName == null || clazz == null) {
            return null;
        }

        Field field = ReflectionUtil.getField(clazz, fieldName);
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getClassAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T targetAnnotation = null;

        List<Annotation> classAnnotations = getClassAnnotations(clazz);
        for (Annotation classAnnotation : classAnnotations) {
            if (annotationClass.isInstance(classAnnotation)) {
                targetAnnotation = (T) classAnnotation;
                break;
            }
        }

        return targetAnnotation;
    }


    public static List<Annotation> getClassAnnotations(Class<?> clazz) {
        if (!ANNOTATIONS_BY_CLASS_MAP.containsKey(clazz)) {
            cacheClassAnnotations(clazz);
        }

        return ANNOTATIONS_BY_CLASS_MAP.get(clazz);
    }

    public static <T extends Annotation> boolean hasAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
        return getFieldAnnotation(clazz, fieldName, annotationClass) != null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Annotation> T getFieldAnnotation(Class<?> clazz, String fieldName, Class<T> annotationClass) {
        T targetAnnotation = null;

        List<Annotation> annotations = getFieldAnnotations(clazz, fieldName);
        for (Annotation annotation : annotations) {
            if (annotationClass.isInstance(annotation)) {
                targetAnnotation = (T) annotation;
                break;
            }
        }

        return targetAnnotation;
    }

    public static List<Annotation> getFieldAnnotations(Class<?> clazz, String fieldName) {

        Map<String, List<Annotation>> annotations = ANNOTATIONS_BY_CLASS_FIELD_MAP.get(clazz);

        if (annotations == null) {
            cacheFieldAnnotations(clazz);
        }

        return ANNOTATIONS_BY_CLASS_FIELD_MAP.get(clazz).get(fieldName);
    }

    private static synchronized void cacheFieldAnnotations(Class<?> clazz) {
        if (!ANNOTATIONS_BY_CLASS_FIELD_MAP.containsKey(clazz)) {
            Map<String, List<Annotation>> annotationsMap = new HashMap<>();
            List<Field> fields = getFields(clazz);

            for (Field field : fields) {
                annotationsMap.put(field.getName(), Arrays.asList(field.getAnnotations()));
            }

            ANNOTATIONS_BY_CLASS_FIELD_MAP.put(clazz, annotationsMap);
        }
    }

    private static synchronized void cacheClassAnnotations(Class<?> clazz) {
        if (!ANNOTATIONS_BY_CLASS_MAP.containsKey(clazz)) {
            ANNOTATIONS_BY_CLASS_MAP.put(clazz, Arrays.asList(clazz.getAnnotations()));
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field targetField = null;
        List<Field> fields = getFields(clazz);

        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                targetField = field;
                break;
            }
        }

        return targetField;
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = CLASS_FIELD_MAP.get(clazz);
        if (fields == null) {
            fields = cacheFields(clazz);
        }

        return fields;
    }

    public static List<Field> getFields(Type type) {
        return getFields(getClassFromType(type));
    }

    public static Class<?> getClassFromType(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            return (Class<?>) type;
        } else {
            return null;
        }
    }

    public static List<String> getFieldNames(Class<?> clazz) {
        List<String> fieldNames = CLASS_FIELD_NAME_MAP.get(clazz);
        if (fieldNames == null) {
            cacheFields(clazz);
            fieldNames = CLASS_FIELD_NAME_MAP.get(clazz);
        }

        return fieldNames;
    }

    private static synchronized <O, I> List<Field> cacheFields(Class<?> clazz) {
        List<Field> totalFields;
        if (!CLASS_FIELD_MAP.containsKey(clazz)) {
            totalFields = new ArrayList<>();
            Class<?> copyClass = clazz;
            while (copyClass != Object.class) {
                totalFields.addAll(Arrays.asList(copyClass.getDeclaredFields()));
                copyClass = copyClass.getSuperclass();
            }
            CLASS_FIELD_MAP.put(clazz, totalFields);

            List<String> fieldNames = new ArrayList<>(totalFields.size());
            for (Field field : totalFields) {
                fieldNames.add(field.getName());
            }
            CLASS_FIELD_NAME_MAP.put(clazz, fieldNames);
        } else {
            totalFields = CLASS_FIELD_MAP.get(clazz);
        }

        return totalFields;
    }

    public static String getSimpleClassName(Object object) {
        if (object == null) {
            return null;
        }

        return getSimpleClassName(object.getClass());
    }

    public static String getSimpleClassName(Class<?> type) {
        if (type == null) {
            return null;
        }

        String simpleName = type.getSimpleName();
        if (StringUtils.isEmpty(simpleName)) {
            String[] nameParts = type.getName().split("\\.");
            simpleName = nameParts[nameParts.length - 1];
        }

        return simpleName;
    }

    public static boolean isValueOrValueContainer(Class<?> type) {
        if (ClassUtils.isPrimitiveOrWrapper(type)) {
            return true;
        }

        return Number.class.isAssignableFrom(type) ||
                CharSequence.class.isAssignableFrom(type) ||
                Date.class.isAssignableFrom(type);
    }

    public static Method getSetter(String fieldName, Class<?> clazz, Class<?> fieldType) {
        String setterName = buildSetterName(fieldName);
        try {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(setterName)) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length == 1 && paramTypes[0].isAssignableFrom(fieldType)) {
                        return method;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Error getting setter method of [{}] class", clazz, e);
            }
            return null;
        }
    }

    private static String buildSetterName(String fieldName) {
        return String.format("set%s%s", Character.toTitleCase(fieldName.charAt(0)), fieldName.substring(1));
    }
}
