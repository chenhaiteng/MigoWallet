package com.chenhaiteng.migowallet

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MigoWalletApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("Migo", "Wallet launched!")
    }
}