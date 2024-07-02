package com.github.calamari34.mantaflipbeta.utils;
import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Object field(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}