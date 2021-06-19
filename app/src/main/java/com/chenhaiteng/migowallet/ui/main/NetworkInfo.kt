package com.chenhaiteng.migowallet.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkInfo private constructor(context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities
        get() = cm.activeNetwork?.let {
            cm.getNetworkCapabilities(it)
        }

    val isConnected
        get() = capabilities?.let{
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } ?: false

    val isWifi
        get() = capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } ?: false

    val isCellular
        get() = capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false

    companion object {
        @Volatile private var standard: NetworkInfo? = null
        fun standard(context: Context? = null) = createSingleton(standard, this) {
            requireNotNull(context)
            standard = NetworkInfo(context!!)

            standard!!
        }
    }
}