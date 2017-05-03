package de.baumann.hhsmoodle;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by juergen on 03.05.17.
 */

public class App extends Application {

    private String mGlobalVarValue;

    public String getGlobalVarValue() {
        return mGlobalVarValue;
    }

    public void setGlobalVarValue(String str) {
        mGlobalVarValue = str;
    }

    private static App mApp = null;
    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }
    public static Context context() {
        return mApp.getApplicationContext();
    }
}
