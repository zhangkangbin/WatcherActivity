package com.zkb.watcheractivity;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class MyWeakReference extends WeakReference<Object> {
    public final String key;
    public final String name;

    public MyWeakReference(String key,Object referent, ReferenceQueue<Object> referenceQueue) {
        super(referent, referenceQueue);
        this.key = key;
        this.name = referent.getClass().getName();
    }


}
