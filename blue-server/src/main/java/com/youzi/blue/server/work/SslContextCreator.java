package com.youzi.blue.server.work;

import com.youzi.blue.server.config.ServerProperties;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

@Slf4j
public class SslContextCreator {
    private static SSLContext sslContext;

    public static SSLContext createSSLContext() {
        return new SslContextCreator().initSSLContext();
    }

    public static SSLContext getSSLContext() {
        if (sslContext == null) {
            sslContext = new SslContextCreator().initSSLContext();
        }
        return sslContext;
    }

    public SSLContext initSSLContext() {
        log.info("Checking SSL configuration properties...");
        final String jksPath = ServerProperties.getInstance().getSslJksPath();
        log.info("Initializing SSL context. KeystorePath = {}.", jksPath);
        if (jksPath == null || jksPath.isEmpty()) {
            // key_store_password or key_manager_password are empty
            log.warn("The keystore path is null or empty. The SSL context won't be initialized.");
            return null;
        }

        // if we have the port also the jks then keyStorePassword and
        // keyManagerPassword
        // has to be defined
        final String keyStorePassword = ServerProperties.getInstance().getSslKeyStorePassword();
        final String keyManagerPassword = ServerProperties.getInstance().getSslKeyManagerPassword();
        if (keyStorePassword == null || keyStorePassword.isEmpty()) {

            // key_store_password or key_manager_password are empty
            log.warn("The keystore password is null or empty. The SSL context won't be initialized.");
            return null;
        }

        if (keyManagerPassword == null || keyManagerPassword.isEmpty()) {

            // key_manager_password or key_manager_password are empty
            log.warn("The key manager password is null or empty. The SSL context won't be initialized.");
            return null;
        }

        // if client authentification is enabled a trustmanager needs to be
        // added to the ServerContext
        boolean needsClientAuth = ServerProperties.getInstance().isSslNeedsClientAuth();

        try {
            log.info("Loading keystore. KeystorePath = {}.", jksPath);
            InputStream jksInputStream = jksDatastore(jksPath);
            SSLContext serverContext = SSLContext.getInstance("TLS");
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(jksInputStream, keyStorePassword.toCharArray());
            log.info("Initializing key manager...");
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyManagerPassword.toCharArray());
            TrustManager[] trustManagers = null;
            if (needsClientAuth) {
                log.warn(
                        "Client authentication is enabled. The keystore will be used as a truststore. KeystorePath = {}.",
                        jksPath);
                // use keystore as truststore, as server needs to trust
                // certificates signed by the
                // server certificates
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
                trustManagers = tmf.getTrustManagers();
            }

            // init sslContext
            log.info("Initializing SSL context...");
            serverContext.init(kmf.getKeyManagers(), trustManagers, null);
            log.info("The SSL context has been initialized successfully.");

            return serverContext;
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyStoreException
                | KeyManagementException | IOException ex) {
            log.error("Unable to initialize SSL context. Cause = {}, errorMessage = {}.", ex.getCause(),
                    ex.getMessage());
            return null;
        }
    }

    private InputStream jksDatastore(String jksPath) throws FileNotFoundException {
        URL jksUrl = getClass().getClassLoader().getResource(jksPath);
        if (jksUrl != null) {
            log.info("Starting with jks at {}, jks normal {}", jksUrl.toExternalForm(), jksUrl);
            return getClass().getClassLoader().getResourceAsStream(jksPath);
        }

        log.warn("No keystore has been found in the bundled resources. Scanning filesystem...");
        File jksFile = new File(jksPath);
        if (jksFile.exists()) {
            log.info("Loading external keystore. Url = {}.", jksFile.getAbsolutePath());
            return new FileInputStream(jksFile);
        }

        log.warn("The keystore file does not exist. Url = {}.", jksFile.getAbsolutePath());
        return null;
    }
}