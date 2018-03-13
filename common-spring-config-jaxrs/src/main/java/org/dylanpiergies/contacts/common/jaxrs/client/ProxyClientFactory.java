package org.dylanpiergies.contacts.common.jaxrs.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.dylanpiergies.contacts.common.logging.LogCorrelationClientRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProxyClientFactory {

    @Value("${client.ssl.key-store:#{null}}")
    private String keystoreFile;

    @Value("${client.ssl.key-store-password:#{null}}")
    private String keystorePassword;

    @Value("${client.ssl.key-store-password:#{null}}")
    private String keyPassword;

    @Value("${client.ssl.key-store-type:#{null}}")
    private String keystoreType;

    @Value("${client.ssl.key-alias:#{null}}")
    private String keyAlias;

    @Value("${client.ssl.trust-store:#{null}}")
    private String truststoreFile;

    @Value("${client.ssl.trust-store-password:#{null}}")
    private String truststorePassword;

    @Value("${client.ssl.trust-store-type:#{null}}")
    private String truststoreType;

    public <T> T createProxyClient(String baseAddress, Class<T> resourceClass) {
        List<Object> providers = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        providers.add(new LogCorrelationClientRequestFilter());
        providers.add(new JacksonJsonProvider(objectMapper));

        T proxy = JAXRSClientFactory.create(baseAddress, resourceClass, providers, true);
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(proxy).getConduit();
        conduit.setTlsClientParameters(createTlsClientParameters());
        return proxy;
    }

    private TLSClientParameters createTlsClientParameters() {
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        try {
            if (keystoreFile != null) {
                if (keystoreType == null) {
                    keystoreType = KeyStore.getDefaultType();
                }
                KeyStore keyStore = KeyStore.getInstance(keystoreType);
                try (FileInputStream stream = new FileInputStream(keystoreFile)) {
                    keyStore.load(stream, keystorePassword.toCharArray());
                }
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, keyPassword.toCharArray());
                tlsClientParameters.setKeyManagers(keyManagerFactory.getKeyManagers());
                if (keyAlias != null) {
                    tlsClientParameters.setCertAlias(keyAlias);
                }
            }

            if (truststoreFile != null) {
                if (truststoreType == null) {
                    truststoreType = KeyStore.getDefaultType();
                }
                KeyStore trustStore = KeyStore.getInstance(truststoreType);
                try (FileInputStream stream = new FileInputStream(truststoreFile)) {
                    trustStore.load(stream, truststorePassword.toCharArray());
                }
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);
                tlsClientParameters.setTrustManagers(trustManagerFactory.getTrustManagers());
            }
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SslConfigurationException(e);
        }
        return tlsClientParameters;
    }
}
