package com.zkb.watcheractivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.squareup.leakcanary.LeakCanary;

import java.lang.ref.ReferenceQueue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "watchActivity1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // LeakCanary.refWatcher(getApplication()).watchFragments(false).buildAndInstall();
        initRegisterActivityLifecycleCallbacks();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, A_Activity.class));
            }
        });
        findViewById(R.id.buttonB).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, B_Activity.class));
            }
        });


    }

    private ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final Set<String> retainedKeys = new CopyOnWriteArraySet<>();

    private void removeKey() {
        MyWeakReference ref;
        if ((ref = (MyWeakReference) queue.poll()) != null) {
            retainedKeys.remove(ref.key);
            Log.d(TAG, "---------------recycle activity----------"+ref.name);
        }

    }

    private void watchActivity(MyWeakReference weakReference) {


        removeKey();

        Runtime.getRuntime().gc();
        enqueueReferences();
        System.runFinalization();

        removeKey();


        if(retainedKeys.contains(weakReference.key)){
            //activity 泄漏
            Log.d(TAG, "-----activity leak----"+weakReference.name );
            Log.d(TAG, "MyWeakReference Activity:    --" + weakReference.get());
        }

        Log.d(TAG, "Activity size:" + ActivityMap.get().getSize());
        Log.d(TAG, "retainedKeys size:" + retainedKeys.size());
    }

    private void initRegisterActivityLifecycleCallbacks() {

        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

                Log.d(TAG, "Destroyed Activity:"+activity.getClass().getName());

                String key = UUID.randomUUID().toString();

                retainedKeys.add(key);


                final MyWeakReference weakReference = new MyWeakReference(key, activity, queue);

                //Log.d(TAG, "MyWeakReference Activity1:    --" + weakReference.get());
                //五秒后去观察，让 gc 飞一会
                new Handler().postDelayed(() -> watchActivity(weakReference),5000);



            }
        });
    }

    private void enqueueReferences() {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new AssertionError();
        }
    }
}
