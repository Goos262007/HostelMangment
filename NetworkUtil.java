package com.hostel.security;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Network Discovery & Port Negotiation Utility: NetworkUtil
 * Ensures the system binds to any user-defined port, resolves port conflicts,
 * and discovers LAN/Wi-Fi IP addresses for multi-device access.
 */
public class NetworkUtil {

    /**
     * Verifies if a given port is available. If unavailable, scans forward to find
     * the next immediately free port. If 0 is passed, assigns any free ephemeral port.
     */
    public static int findAvailablePort(int preferredPort) {
        if (preferredPort <= 0) {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            } catch (IOException e) {
                return 8443;
            }
        }

        int port = preferredPort;
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            if (isPortAvailable(port)) {
                if (port != preferredPort) {
                    System.out.println("[NetworkUtil] Notice: Preferred port " + preferredPort +
                        " was busy. Automatically switched to free port: " + port);
                }
                return port;
            }
            port++;
        }

        // Fallback to dynamic OS allocation
        try (ServerSocket socket = new ServerSocket(0)) {
            int allocated = socket.getLocalPort();
            System.out.println("[NetworkUtil] Assigned dynamic OS port: " + allocated);
            return allocated;
        } catch (IOException e) {
            return preferredPort;
        }
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))) {
            ss.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Discovers all non-loopback IPv4 addresses across all active network interfaces.
     */
    public static List<String> getLocalIpAddresses() {
        List<String> addresses = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;

                Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress addr = inetAddresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String hostAddress = addr.getHostAddress();
                        if (!hostAddress.startsWith("169.254.")) { // Skip link-local
                            addresses.add(hostAddress);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("[NetworkUtil] Could not enumerate network interfaces: " + e.getMessage());
        }

        if (addresses.isEmpty()) {
            addresses.add("127.0.0.1");
        }
        return addresses;
    }

    /**
     * Returns the primary Wi-Fi / Ethernet LAN IP for multi-device cross-network connections.
     */
    public static String getPrimaryLocalIp() {
        List<String> ips = getLocalIpAddresses();
        return ips.isEmpty() ? "127.0.0.1" : ips.get(0);
    }
}
