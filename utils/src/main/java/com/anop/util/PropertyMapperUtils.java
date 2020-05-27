package com.anop.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 属性映射帮助工具
 *
 * @author Xue_Feng
 */
public class PropertyMapperUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(PropertyMapperUtils.class);
    private static final Pattern GET_PATTERN = Pattern.compile("get\\w+");
    private static final String GET = "get";
    private static final String SET = "set";
    private static final int COMMON_PUBLIC_METHOD_AMOUNT = 9;

    public static void map(Object source, Object target) {
        if (source == null || target == null) {
            throw new NullPointerException("source or target must not be null");
        }
        Class<?> aClass = source.getClass();
        Class<?> bClass = target.getClass();
        Method[] methods = aClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method getMethod = methods[i];
            if (GET_PATTERN.matcher(getMethod.getName()).matches()) {
                String setMethodName = getMethod.getName().replace(GET, SET);
                Class<?> returnType = getMethod.getReturnType();
                try {
                    Method setMethod = bClass.getMethod(setMethodName, returnType);
                    Object value = getMethod.invoke(source);
                    setMethod.invoke(target, value);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    LOGGER.info("mapping failed:" + e.getMessage());
                }
            }
        }
    }

    public static <S, T> Collection<T> map(Collection<S> source, Class<S> sourceClass, Class<T> targetClass) {
        if (sourceClass == null || targetClass == null) {
            throw new NullPointerException("sourceClass or targetClass must not be null");
        }
        if (source == null) {
            throw new NullPointerException("source or target must not be null");
        }
        Collection<T> target = new ArrayList<>(source.size());
        if (source.size() < 1) {
            return target;
        }
        Method[] methods = sourceClass.getMethods();
        List<Method> getMethods = new ArrayList<>(methods.length - COMMON_PUBLIC_METHOD_AMOUNT);
        List<Method> setMethods = new ArrayList<>(methods.length - COMMON_PUBLIC_METHOD_AMOUNT);
        for (int i = 0; i < methods.length; i++) {
            if (GET_PATTERN.matcher(methods[i].getName()).matches()) {
                String setMethodName = methods[i].getName().replace(GET, SET);
                try {
                    Method setMethod = targetClass.getMethod(setMethodName, methods[i].getReturnType());
                    getMethods.add(methods[i]);
                    setMethods.add(setMethod);
                } catch (NoSuchMethodException e) {
                    LOGGER.info("mapping failed:" + e.getMessage());
                }
            }
        }
        for (S item : source) {
            try {
                T instance = targetClass.newInstance();
                for (int i = 0; i < getMethods.size(); i++) {
                    setMethods.get(i).invoke(instance, getMethods.get(i).invoke(item));
                }
                target.add(instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.info("can not create instance of " + e.getMessage());
                return null;
            }
        }
        return target;
    }

    public static <T> T map(Object source, Class<T> targetClass) {
        if (targetClass == null) {
            throw new NullPointerException("targetClass must not be null");
        }
        T target = null;
        try {
            target = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.info("can not create instance of " + e.getMessage());
            return null;
        }
        map(source, target);
        return target;
    }

    public static boolean hasProperty(String name, Class<?> targetClass) {
        if (targetClass == null) {
            throw new NullPointerException("targetClass must not be null");
        }
        if (StringUtils.isNullOrWhiteSpace(name)) {
            throw new IllegalArgumentException("property must not be blank");
        }
        name = name.trim();
        if (Character.isLowerCase(name.charAt(0))) {
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        try {
            Method property = targetClass.getMethod(GET + name.trim());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static String getUnderscoreFormat(String source) {
        if (StringUtils.isNullOrWhiteSpace(source)) {
            throw new IllegalArgumentException("source must not be blank");
        }
        source = source.trim();
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < source.length(); i++) {
            if (!Character.isUpperCase(source.charAt(i))) {
                sb.append(source.charAt(i));
            } else {
                if (i != 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(source.charAt(i)));
            }
        }
        return sb.toString();
    }
}
