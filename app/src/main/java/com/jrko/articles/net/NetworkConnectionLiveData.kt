package com.jrko.articles.net

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * A LiveData object that ViewModels can observe to monitor
 * network change events.
 */
class NetworkConnectionLiveData(private val context: Context) : LiveData<Boolean>() {
    private var connectivityManager: ConnectivityManager? = context.getSystemService(CONNECTIVITY_SERVICE)?.let {
        it as ConnectivityManager
    }
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateConnection()
        }
    }

    override fun onActive() {
        super.onActive()
        updateConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager?.registerDefaultNetworkCallback(getConnectivityManagerCallback())
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> handleLollipopNetworkAvailabilityRequest()
            else -> {
                context.registerReceiver(networkReceiver, IntentFilter("android.net.conn.CONNECTIVITY_ACTION"))
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager?.unregisterNetworkCallback(connectivityManagerCallback)
        } else {
            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun updateConnection() {
        var isConnected = false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager?.activeNetworkInfo
            networkInfo?.apply {
                @Suppress("DEPRECATION")
                isConnected =
                    networkInfo.isConnected && (this.type == ConnectivityManager.TYPE_MOBILE || this.type == ConnectivityManager.TYPE_WIFI)
            }
        } else {
            val activeNetwork = connectivityManager?.activeNetwork
            activeNetwork?.apply {
                val networkCapabilities = connectivityManager?.getNetworkCapabilities(this)
                networkCapabilities?.apply {
                    isConnected = (this.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || this.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    ))
                }
            }
        }
        postValue(isConnected)
    }

    private fun getConnectivityManagerCallback(): ConnectivityManager.NetworkCallback {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network?) {
                    postValue(true)
                }

                override fun onLost(network: Network?) {
                    postValue(false)
                }
            }
            return connectivityManagerCallback
        } else {
            throw IllegalAccessError("Have you ever felt like you don't belong here?")
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleLollipopNetworkAvailabilityRequest() {
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager?.registerNetworkCallback(builder.build(), getConnectivityManagerCallback())
    }
}