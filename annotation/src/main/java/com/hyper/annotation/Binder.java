package com.hyper.annotation;

public class Binder {

    public static final String SUFFIX = "$$Binder";

    public static void Bind( Object obj ) {

        // pending: cache created instances.

        String str = obj.getClass().getCanonicalName();
        String clazz = str + SUFFIX;

        try {
            Class _c = Class.forName( clazz );
            IBinder binder = (IBinder)_c.newInstance();
            binder.bind( obj );
        } catch( Exception x ) {
            x.printStackTrace();
        }
    }
}
