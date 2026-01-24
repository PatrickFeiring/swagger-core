package io.swagger.v3.core.modern;

import java.util.Set;

public class AnnotationUtils {

    public static boolean isSetType(Class<?> clazz) {
        if (clazz.equals(Set.class)) {
            return true;
        }
        for (Class<?> a : clazz.getInterfaces()) {
            if (Set.class.equals(a)) {
                return true;
            }
        }
        return false;
    }
}
