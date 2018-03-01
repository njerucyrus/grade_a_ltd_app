package com.me.njerucyrus.gradea;


import android.app.Activity;

/**
 * Created by njerucyrus on 2/5/18.
 */

public class Config extends Activity {
    public String BASE_URL = "http://grade.hudutech.com/api_backend/api";

    public Config() {
    }

    public String getBASE_URL() {
        return this.BASE_URL;
    }
}
