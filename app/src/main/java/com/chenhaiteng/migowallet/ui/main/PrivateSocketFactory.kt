// References:
// https://github.com/yschimke/okurl/blob/master/src/main/kotlin/com/baulsupp/okurl/network/InterfaceSocketFactory.kt
// https://stackoverflow.com/questions/30118534/global-state-and-local-address-in-okhttp
package com.chenhaiteng.migowallet.ui.main

import com.chenhaiteng.migowallet.utility.NetworkInfo
import java.math.BigInteger
import java.net.*
import javax.net.SocketFactory

class PrivateSocketFactory(private val localAddress:InetAddress) : SocketFactory() {

    private val system = getDefault()

    override fun createSocket(): Socket = system.createSocket().apply {
        bind(InetSocketAddress(localAddress, 0))
    }

    override fun createSocket(host: String?, port: Int): Socket = system.createSocket(host,port,localAddress, 0)

    override fun createSocket(address: InetAddress?, port: Int): Socket = system.createSocket(address, port, localAddress, 0)

    override fun createSocket(host: String?,
                              port: Int,
                              localAddress: InetAddress?,
                              localPort: Int): Socket {
        return system.createSocket(host, port, localAddress, localPort)
    }

    override fun createSocket(address: InetAddress?,
                              port: Int,
                              localAddress: InetAddress?,
                              localPort: Int): Socket {
        return system.createSocket(address, port, localAddress, localPort)
    }

    companion object {

        fun wifi() : SocketFactory? {
            for ( item in NetworkInterface.getNetworkInterfaces()) {
                item.getIP()?.let {
                    val addrInt = it.address.toAddressInt()
                    val wifiInt = NetworkInfo.standard().wifiIP
                    val wifiReverse = wifiInt.reverseBytes()
                    if (addrInt == wifiInt || addrInt == wifiReverse) {
                        return PrivateSocketFactory(item.inetAddresses.nextElement())
                    }
                }
            }
            return  null
        }
        fun byName(ipOrInterface: String): SocketFactory? {
            val localAddress = try {
                InetAddress.getByName(ipOrInterface)
            } catch (uhe: UnknownHostException) {
                val networkInterface = NetworkInterface.getByName(ipOrInterface) ?: return null
                networkInterface.inetAddresses.nextElement()
            }

            return PrivateSocketFactory(localAddress)
        }
    }
}

fun ByteArray.toAddressInt() : Int? {
    if(count() <4 ) return null
    return BigInteger(this).toInt()
}
fun Int.reverseBytes(): Int {
    val v0 = ((this ushr 0) and 0xFF)
    val v1 = ((this ushr 8) and 0xFF)
    val v2 = ((this ushr 16) and 0xFF)
    val v3 = ((this ushr 24) and 0xFF)
    return (v0 shl 24) or (v1 shl 16) or (v2 shl 8) or (v3 shl 0)
}

fun NetworkInterface.getIP() : InetAddress? {
    for ( addr in inetAddresses) {
        if ( !addr.isLoopbackAddress && addr.hostAddress.indexOf(":") == -1 ) {
            return addr
        }
    }
    return null
}