package edu2.innotech;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class MakeCache implements InvocationHandler {
    private Object obj;

    private HashMap<Method, Object> hashMapMethodsValues = new HashMap<>();
    private boolean isChanged = true;

    public MakeCache(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class myObjClass = obj.getClass();
        Method m = myObjClass.getDeclaredMethod(method.getName(), method.getParameterTypes());

        if (m.isAnnotationPresent(Mutator.class)) {
            isChanged = true;
            hashMapMethodsValues.clear();
            return method.invoke(obj, args);
        }

        Object methodValue;
        if (m.isAnnotationPresent(Cache.class) && !isChanged && hashMapMethodsValues.containsKey(method)) {
            methodValue = hashMapMethodsValues.get(method);
        } else {
            methodValue = method.invoke(obj, args);
            hashMapMethodsValues.put(method, methodValue);
            isChanged = false;
        };
        return methodValue;
    }
}
