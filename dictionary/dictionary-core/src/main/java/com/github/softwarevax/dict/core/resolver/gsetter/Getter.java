package com.github.softwarevax.dict.core.resolver.gsetter;


import com.github.softwarevax.dict.core.utils.Assert;

public class Getter {

    public static String getString(Object obj) {
        Assert.notNull(obj, "obj值不能为空");
        return String.valueOf(obj);
    }

    public static Boolean getBoolean(Object obj) {
        // obj的类型是所有可以转为boolean的类型
        Assert.notNull(obj, "obj值不能为空");
        Class clazz = obj.getClass();
        if(Boolean.class == clazz) {
            return (Boolean) obj;
        } else if(String.class == clazz) {
            // 字符串转布尔
            return Boolean.parseBoolean((String) obj);
        } else if(clazz == Integer.class) {
            // 整数转布尔
            return (Integer)obj > 0 ? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Byte.class) {
            // Byte转布尔
            return ((Byte)obj).intValue() > 0? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Short.class) {
            // Short转布尔
            return ((Short)obj).intValue() > 0? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Character.class) {
            // Character转布尔
            return ((Character)obj).charValue() > 0? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Long.class) {
            // Long转布尔
            return ((Long)obj).intValue() > 0? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Float.class) {
            // Float转布尔
            return ((Float)obj).intValue() > 0? Boolean.TRUE : Boolean.FALSE;
        } else if(clazz == Double.class) {
            // Double转布尔
            return ((Double)obj).intValue() > 0? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }

    public static void main(String[] args) {
        boolean a = true;
        byte b = 0;
        short c = 12;
        char d = 'c';
        int e = 2;
        long f = 2l;
        float g = 2;
        double h = 23d;
        String s = "true";

        // 转字符串
        /*System.out.println(Getter.getString(a));
        System.out.println(Getter.getString(b));
        System.out.println(Getter.getString(c));
        System.out.println(Getter.getString(d));
        System.out.println(Getter.getString(e));
        System.out.println(Getter.getString(f));
        System.out.println(Getter.getString(g));
        System.out.println(Getter.getString(h));*/

        // 转布尔
       /* System.out.println(Getter.getBoolean(a));
        System.out.println(Getter.getBoolean(b));
        System.out.println(Getter.getBoolean(c));
        System.out.println(Getter.getBoolean(d));
        System.out.println(Getter.getBoolean(e));
        System.out.println(Getter.getBoolean(f));
        System.out.println(Getter.getBoolean(g));
        System.out.println(Getter.getBoolean(h));
        System.out.println(Getter.getBoolean(s));*/

    }

}
