package com.nixsolutions.reflection;

import com.nixsolutions.ppp.reflection.Ignore;
import com.nixsolutions.ppp.reflection.Info;
import com.nixsolutions.ppp.reflection.ReflectionUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectionUtilImpl implements ReflectionUtil {

    @Override public String toString(Object object) {
        Class<?> aClass = object.getClass();
        StringBuilder stringBuilder = new StringBuilder(ELEMENT_START);
        for (Field field : aClass.getDeclaredFields()) {
            for (Annotation declaredAnnotation : field
                    .getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType().getName()
                        .equals("com.nixsolutions.ppp.reflection.Info")) {
                    if (isBaseElement(field.getType())) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(object);
                            stringBuilder.append(field.getName())
                                    .append(KEY_VALUE_SEPARATOR).append(value)
                                    .append(ELEMENT_SEPARATOR).append(" ");
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        String string = field.getName();
                        try {
                            stringBuilder.append(string)
                                    .append(KEY_VALUE_SEPARATOR)
                                    .append(toString(field.get(object)))
                                    .append(ELEMENT_SEPARATOR).append(" ");
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        String string = stringBuilder.append(ELEMENT_END).toString();
        return string
                .replaceAll(ELEMENT_SEPARATOR + " " + ELEMENT_END, ELEMENT_END);
    }

    @Override public boolean isTheSame(Object object1, Object object2) {
        return fieldsWithoutIgnore(object1)
                .equals(fieldsWithoutIgnore(object2));
    }

    private String fieldsWithoutIgnore(Object object) {
        Class<?> aClass = object.getClass();
        StringBuilder stringBuilder = new StringBuilder(ELEMENT_START);
        for (Field field : aClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class) && (
                    !field.isAnnotationPresent(Info.class) || field
                            .isAnnotationPresent(Info.class))) {
                if (isBaseElement(field.getType())) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(object);
                        stringBuilder.append(field.getType())
                                .append(KEY_VALUE_SEPARATOR).append(value)
                                .append(ELEMENT_SEPARATOR).append(" ");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        stringBuilder.append(field.getType())
                                .append(KEY_VALUE_SEPARATOR)
                                .append(fieldsWithoutIgnore(field.get(object)))
                                .append(ELEMENT_SEPARATOR).append(" ");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        String string = stringBuilder.append(ELEMENT_END).toString();
        return string
                .replaceAll(ELEMENT_SEPARATOR + " " + ELEMENT_END, ELEMENT_END);
    }

}
