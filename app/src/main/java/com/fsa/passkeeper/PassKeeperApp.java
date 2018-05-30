package com.fsa.passkeeper;

import android.app.Application;
import android.content.Context;

import com.fsa.passkeeper.Database.IDatabaseContext;
import com.fsa.passkeeper.Database.MainDbContext;
import com.fsa.passkeeper.Database.MockDbContext;

public class PassKeeperApp extends Application {

    private static IDatabaseContext mDbContext;

    @Override
    public void onCreate() {
        //mDbContext = new MockDbContext();
        mDbContext = new MainDbContext(this);

        super.onCreate();
    }

    public static IDatabaseContext getDatabaseContext() {
        return mDbContext;
    }

}