package cn.whiteg.mmocore.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker<RT> {
    final Method handle;

    public MethodInvoker(Method handle) {
        this.handle = handle;
    }

    public RT invoke(Object obj,Object... args) {
        try{
            //noinspection unchecked
            return (RT) handle.invoke(obj,args);
        }catch (IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException("Cannot invoke method " + handle.getName(),e);
        }
    }
}
