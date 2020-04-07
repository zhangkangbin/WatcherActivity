package com.zkb.watcheractivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.squareup.leakcanary.HeapDumper.RETRY_LATER;
import static com.squareup.leakcanary.Retryable.Result.RETRY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "watchActivity1";
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAppPermissions();
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
            Log.d(TAG, "---------------recycle activity----------" + ref.name);
        }

    }

    private void watchActivity(MyWeakReference weakReference) {


        removeKey();

        Runtime.getRuntime().gc();
        enqueueReferences();
        System.runFinalization();

        removeKey();


        if (retainedKeys.contains(weakReference.key)) {
            //activity 泄漏
            Log.d(TAG, "-----activity leak----" + weakReference.name);
            Log.d(TAG, "MyWeakReference Activity:    --" + weakReference.get());
            File storageDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/watchActivity");

            if(!storageDirectory.exists()){
                storageDirectory.mkdir();
            }

            File heapDumpFile = new File(storageDirectory, UUID.randomUUID().toString() + ".hprof");

            if (heapDumpFile == null) return;
            try {
                Log.d(TAG, "---dumpHprofData -------" + heapDumpFile.getAbsolutePath());
                Debug.dumpHprofData(heapDumpFile.getAbsolutePath());

                if (storageDirectory.list() != null) {

                    for (String name : storageDirectory.list()) {
                        Log.d(TAG, "name :" + name);
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }


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

                Log.d(TAG, "Destroyed Activity:" + activity.getClass().getName());

                String key = UUID.randomUUID().toString();

                retainedKeys.add(key);


                final MyWeakReference weakReference = new MyWeakReference(key, activity, queue);

                //Log.d(TAG, "MyWeakReference Activity1:    --" + weakReference.get());
                //五秒后去观察，让 gc 飞一会
                new Handler().postDelayed(() -> watchActivity(weakReference), 5000);


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

    private void requestAppPermissions() {
        Dexter.withActivity(this)
                .withPermissions(PERMISSIONS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //  initView();
                            Toast.makeText(getApplicationContext(), "权限获取成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "权限获取失败", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    }
                })
                .check();
    }

}
