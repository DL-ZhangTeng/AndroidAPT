package com.zhangteng.library;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BindViewTool {
    /**
     * 通过反射调用Apt生成的代码以执行绑定视图逻辑
     *
     * @param activity
     */
    public static void bind(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        try {
            Class<?> bindViewClass = Class.forName(clazz.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射调用Apt生成的代码以执行绑定视图逻辑
     *
     * @param fragment
     */
    public static void bind(Fragment fragment) {
        Class<? extends Fragment> clazz = fragment.getClass();
        try {
            Class<?> bindViewClass = Class.forName(clazz.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bind", fragment.getClass());
            method.invoke(bindViewClass.newInstance(), fragment);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
