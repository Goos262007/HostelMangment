package com.hostel;

import com.hostel.config.DatabaseConnection;
import com.hostel.security.NetworkUtil;
import com.hostel.security.SslContextHelper;
import com.hostel.server.ApiHandler;
import com.hostel.server.StaticFileHandler;
import com.hostel.service.HostelManagementService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main Application Launcher.
 * Features:
 * 1. Dual-Port Architecture: Runs standard warning-free HTTP (port 8080) and
 *    TLS 1.3 Encrypted HTTPS (port 8443) simultaneously.
 * 2. Multi-Device Network Binding (0.0.0.0): Accessible from any phone, tablet, or laptop on LAN/Wi-Fi.
 * 3. Dynamic Port Negotiation: Resolves conflicts automatically so it works on any port.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("  AEGIS HOSTEL MANAGEMENT SYSTEM (B.Tech CSE PBL Project)         ");
        System.out.println("  Dual-Mode Server (Warning-Free HTTP + Encrypted HTTPS TLS 1.3) ");
        System.out.println("==================================================================");

        try {
            DatabaseConnection dbConfig = DatabaseConnection.getInstance();

            // Default ports: 8080 (Clean HTTP) and 8443 (Encrypted HTTPS)
            int defaultHttpPort = 8080;
            int defaultHttpsPort = 8443;

            // Handle CLI argument for port override
            if (args != null && args.length > 0) {
                try {
                    int p = Integer.parseInt(args[0].trim());
                    if (p > 0) {
                        defaultHttpPort = p;
                        defaultHttpsPort = (p == 8080) ? 8443 : (p + 363);
                    }
                } catch (NumberFormatException ignored) {}
            }

            int httpPort = NetworkUtil.findAvailablePort(defaultHttpPort);
            int httpsPort = NetworkUtil.findAvailablePort(defaultHttpsPort);

            // Initialize business layer & handlers
            HostelManagementService service = new HostelManagementService();
            ApiHandler apiHandler = new ApiHandler(service);
            StaticFileHandler staticHandler = new StaticFileHandler("web");
            ExecutorService threadPool = Executors.newFixedThreadPool(16);

            // 1. Start Standard HTTP Server (Zero Browser Warnings on Any Device)
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", httpPort), 0);
            httpServer.createContext("/api", apiHandler);
            httpServer.createContext("/", staticHandler);
            httpServer.setExecutor(threadPool);
            httpServer.start();

            // 2. Start Encrypted HTTPS Server (TLS 1.3 / 2048-bit RSA)
            boolean httpsStarted = false;
            try {
                SSLContext sslContext = SslContextHelper.createSSLContext(
                    dbConfig.getKeystorePath(),
                    dbConfig.getKeystorePassword()
                );
                HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress("0.0.0.0", httpsPort), 0);
                HttpsConfigurator configurator = SslContextHelper.createHttpsConfigurator(sslContext);
                httpsServer.setHttpsConfigurator(configurator);
                httpsServer.createContext("/api", apiHandler);
                httpsServer.createContext("/", staticHandler);
                httpsServer.setExecutor(threadPool);
                httpsServer.start();
                httpsStarted = true;
            } catch (Exception e) {
                System.err.println("[Notice] HTTPS could not be initialized: " + e.getMessage());
            }

            // Discover local network IPs for phones / tablets
            List<String> lanIps = NetworkUtil.getLocalIpAddresses();
            String primaryIp = NetworkUtil.getPrimaryLocalIp();

            System.out.println();
            System.out.println("  ===============================================================");
            System.out.println("  >>> DUAL-MODE SERVER RUNNING SUCCESSFULLY! <<<");
            System.out.println("  ===============================================================");
            System.out.println();
            System.out.println("  Option 1: CLEAN ACCESS (100% Warning-Free on all devices):");
            System.out.println("  -> Local PC:  http://localhost:" + httpPort + "/");
            System.out.println("  -> Mobile/LAN: http://" + primaryIp + ":" + httpPort + "/");
            System.out.println();
            if (httpsStarted) {
                System.out.println("  Option 2: TLS ENCRYPTED ACCESS (TLS 1.3 / 2048-bit RSA):");
                System.out.println("  -> Local PC:  https://localhost:" + httpsPort + "/");
                System.out.println("  -> Mobile/LAN: https://" + primaryIp + ":" + httpsPort + "/");
                System.out.println();
            }
            System.out.println("  Admin Login ID      : admin");
            System.out.println("  Admin Password      : admin123");
            System.out.println("  Database Mode       : " + dbConfig.getStatusMessage());
            System.out.println("  ===============================================================");
            System.out.println("  WHY HTTPS SHOWS 'NOT SECURE' IN LOCAL DEVELOPMENT:");
            System.out.println("  All local SSL certificates are self-signed. If you open HTTPS,");
            System.out.println("  click 'Advanced' -> 'Proceed to localhost (unsafe)'.");
            System.out.println("  To avoid any warning screen completely, simply use Option 1 (HTTP).");
            System.out.println("  ===============================================================");

        } catch (Exception e) {
            System.err.println("[FATAL] Server startup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
