package com.chenhaiteng.migowallet.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class NetworkInfo @Inject constructor(@ApplicationContext context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val capabilities
        get() = cm.activeNetwork?.let {
            cm.getNetworkCapabilities(it)
        }

    private val wifiManger = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiIP
        get() = wifiManger.connectionInfo.ipAddress

    val isConnected
        get() = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false

    val isWifi
        get() = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false

    val isCellular
        get() = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
}