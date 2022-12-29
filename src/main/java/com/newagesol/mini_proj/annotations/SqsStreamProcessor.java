package com.newagesol.mini_proj.annotations;

import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqsStreamProcessor {

    public static List<Method> getMethodsAnnotatedWithSqsStream(final Class<?> type) {
        final List<Method> methods = new ArrayList<>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(SqsStream.class)) {
                    SqsStream annotInstance = method.getAnnotation(SqsStream.class);
//                    if (annotInstance.x() == 3 && annotInstance.y() == 2) {
//                        methods.add(method);
//                    }
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }
}
