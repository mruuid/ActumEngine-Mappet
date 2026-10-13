package unkn0wn.ae.utils;

import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionUtilsAnimation {
    public ReflectionUtilsAnimation() {
    }

    public static Method getMethod(Class<?> clazz, String methodName, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, (Class[]) Arrays.stream(args).map(Object::getClass).toArray((x$0) -> new Class[x$0]));
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName) {
        return getMethod(clazz, methodName);
    }

    public static Object getAndInvokeMethod(Class<?> clazz, String methodName, Object target, Object... args) {
        try {
            Method method = getMethod(clazz, methodName, args);
            return method.invoke(target, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getAndInvokeMethod(Class<?> clazz, String methodName, Object target) {
        return getAndInvokeMethod(clazz, methodName, target);
    }
}
