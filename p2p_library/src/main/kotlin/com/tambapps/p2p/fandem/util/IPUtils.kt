package com.tambapps.p2p.fandem.util

import com.tambapps.p2p.fandem.Peer
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.SocketException
import java.util.*

object IPUtils {//is ipv4
  /**
   * from https://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device-from-code
   * @return return the ip address of the device
   */
  @JvmStatic
  @get:Throws(SocketException::class)
  val iPAddress: InetAddress?
    get() {
      val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
      for (intf in interfaces) {
        val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
        for (addr in addrs) {
          if (!addr.isLoopbackAddress) {
            if (addr.hostAddress.indexOf(':') < 0) { //is ipv4
              return addr
            }
          }
        }
      }
      return null
    }

  /**
   * Get an available port
   * @param inetAddress the address of the host
   * @return an available port
   */
  @JvmStatic
  fun getAvailablePort(inetAddress: InetAddress?): Int {
    var port = Peer.DEFAULT_PORT
    while (port < Peer.DEFAULT_PORT + 16 * 16) {
      try {
        ServerSocket(port, 0, inetAddress).use { }
      } catch (e: IOException) {
        port++
        continue
      }
      return port
    }
    throw RuntimeException("No available port was found")
  }

  @JvmStatic
  @get:Throws(SocketException::class)
  val availablePeer: Peer?
    get() {
      val address = iPAddress
      return if (address == null) null else Peer.of(address, getAvailablePort(address))
    }

  /**
   * Returns a well formatted string of the given ip
   * @param inetAddress the address
   * @return a well formatted string of the given ip
   */
  @JvmStatic
  fun toString(inetAddress: InetAddress): String {
    return inetAddress.hostAddress.replace("/", "")
  }

  /**
   * Returns the hex string of the given ip
   * @param inetAddress the address
   * @return the hex string of the given ip
   */
  @JvmStatic
  fun toHexString(inetAddress: InetAddress): String {
    return toString(inetAddress).split(".").joinToString(prefix = "", postfix = "", separator = "") { s -> toHexString(s) }
  }

  @JvmStatic
  fun toHexString(s: String): String {
    return toHexString(s.toInt())
  }

  @JvmStatic
  fun toHexString(i: Int): String {
    val  n = i.toString(16)
    return if (n.length == 1) "0$n" else n
  }
}