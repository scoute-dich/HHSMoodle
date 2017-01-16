package de.baumann.hhsmoodle.helper;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RebootReceiver_not extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, class_NotService.class);
        serviceIntent.putExtra("caller", "RebootReceiver_not");
        context.startService(serviceIntent);
    }
}