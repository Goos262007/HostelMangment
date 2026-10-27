package com.hostel.security;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * SSL/TLS Configuration Coordinator: SslContextHelper
 * Manages modern TLS 1.3 / TLS 1.2 cryptographic handshakes for cross-device encryption.
 */
public class SslContextHelper {

    public static SSLContext createSSLContext(String keystorePath, String password) throws Exception {
        File ksFile = resolveKeystoreFile(keystorePath);

        if (!ksFile.exists()) {
            System.err.println("[SslContextHelper] Keystore not found at: " + ksFile.getAbsolutePath());
            System.err.println("[SslContextHelper] Generating self-signed TLS PKCS12 keystore automatically...");
            generateKeystore(ksFile.getAbsolutePath(), password);
        }

        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream in = new FileInputStream(ksFile)) {
            ks.load(in, password.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

    public static HttpsConfigurator createHttpsConfigurator(SSLContext sslContext) {
        return new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                try {
                    SSLContext ctx = getSSLContext();
                    SSLParameters sslParams = ctx.getDefaultSSLParameters();
                    sslParams.setNeedClientAuth(false);
                    sslParams.setProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
                    params.setSSLParameters(sslParams);
                } catch (Exception ex) {
                    System.err.println("[HttpsConfigurator] Failed to configure HTTPS parameters: " + ex.getMessage());
                }
            }
        };
    }

    private static File resolveKeystoreFile(String configuredPath) {
        File[] candidates = {
            new File(configuredPath),
            new File("config/keystore.p12"),
            new File("../config/keystore.p12"),
            new File("keystore.p12")
        };
        for (File candidate : candidates) {
            if (candidate.exists()) {
                return candidate;
            }
        }
        return new File("config/keystore.p12");
    }

    private static void generateKeystore(String targetPath, String password) {
        try {
            File target = new File(targetPath);
            File parent = target.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            String javaHome = System.getProperty("java.home");
            File keytoolExe = new File(javaHome, "bin/keytool.exe");
            if (!keytoolExe.exists()) {
                keytoolExe = new File(javaHome, "bin/keytool");
            }

            String keytoolCmd = keytoolExe.exists() ? keytoolExe.getAbsolutePath() : "keytool";

            ProcessBuilder pb = new ProcessBuilder(
                keytoolCmd,
                "-genkeypair",
                "-alias", "hostel",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "3650",
                "-keystore", target.getAbsolutePath(),
                "-storetype", "PKCS12",
                "-storepass", password,
                "-keypass", password,
                "-dname", "CN=HostelServer, OU=PBL, O=College, L=Campus, ST=State, C=IN",
                "-ext", "SAN=dns:localhost,ip:127.0.0.1,ip:0.0.0.0"
            );
            pb.inheritIO();
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                System.out.println("[SslContextHelper] Keystore successfully created at: " + target.getAbsolutePath());
            } else {
                System.err.println("[SslContextHelper] Keytool exited with code: " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("[SslContextHelper] Could not auto-generate keystore: " + e.getMessage());
        }
    }
}
