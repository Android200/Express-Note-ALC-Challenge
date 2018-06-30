package com.alc.app.expressnote.offline;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Umar Saidu Auna on 06/26/18.
 */

public class Firediary extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



    }
}
