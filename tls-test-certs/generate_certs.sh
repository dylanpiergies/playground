#!/bin/bash

set -e

if [[ -z "$PASSWORD" ]]; then
    PASSWORD=password
fi

if [[ -z "$CERTS_DIR" ]]; then
    CERTS_DIR='.'
fi

if [[ -z "$BUILD_KEYSTORE" ]]; then
    BUILD_KEYSTORE="$CERTS_DIR/build_keystore.jks"
fi

if [[ -z "$HOST_KEYSTORE" ]]; then
    HOST_KEYSTORE="$CERTS_DIR/host_keystore.jks"
fi

if [[ -z "$CLIENT_KEYSTORE" ]]; then
    CLIENT_KEYSTORE="$CERTS_DIR/client.p12"
fi

if [[ -z "$CN_HOST" ]]; then
    CN_HOST='localhost'
fi

if [[ -z "$CN_CLIENT" ]]; then
    CN_CLIENT=client
fi

if [[ -z "$TRUSTSTORE" ]]; then
    TRUSTSTORE="$CERTS_DIR/truststore.jks"
fi

set -u

DNAME_CA='CN=Dylan Piergies Test CA,OU=ca.test.dylanpiergies.org,O=Dylan Piergies,L=Bristol,C=GB'
DNAME_HOST="CN=$CN_HOST,OU=test.dylanpiergies.org,O=Dylan Piergies,L=Bristol,C=GB"
DNAME_CLIENT="CN=client,OU=test.dylanpiergies.org,O=Dylan Piergies,L=Bristol,C=GB"

set -x

if [ ! -f "$TRUSTSTORE" ]; then
    # Generate a certificate authority (CA)
    keytool -genkey -alias ca -ext BC=ca:true \
        -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass "$PASSWORD" \
        -validity 3650 -dname "$DNAME_CA" \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Export certificate authority
    keytool -export -alias ca -file "$CERTS_DIR/ca.crt" -rfc \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Import certificate authority into a new truststore
    keytool -import -trustcacerts -noprompt -alias ca -file "$CERTS_DIR/ca.crt" \
        -keystore "$TRUSTSTORE" -storepass "$PASSWORD"
fi

if [ ! -f "$HOST_KEYSTORE" ]; then
    # Generate a host certificate
    keytool -genkey -alias "$CN_HOST" \
        -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass "$PASSWORD" \
        -validity 3650 -dname "$DNAME_HOST" \
        -keystore "$HOST_KEYSTORE" -storepass "$PASSWORD"

    # Generate a host certificate signing request
    keytool -certreq -alias "$CN_HOST" -ext BC=ca:true \
        -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
        -validity 3650 -file "$CERTS_DIR/$CN_HOST.csr" \
        -keystore "$HOST_KEYSTORE" -storepass "$PASSWORD"

    # Generate signed certificate with the certificate authority
    keytool -gencert -alias ca \
        -validity 3650 -sigalg SHA512withRSA \
        -infile "$CERTS_DIR/$CN_HOST.csr" -outfile "$CERTS_DIR/$CN_HOST.crt" -rfc \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Import certificate authority into host keystore
    keytool -import -trustcacerts -noprompt -alias ca -file "$CERTS_DIR/ca.crt" \
        -keystore "$HOST_KEYSTORE" -storepass "$PASSWORD"

    # Import signed certificate into the keystore
    keytool -import -trustcacerts -alias "$CN_HOST" \
        -file "$CERTS_DIR/$CN_HOST.crt" \
        -keystore "$HOST_KEYSTORE" -storepass "$PASSWORD"
fi

if [ ! -f "$CLIENT_KEYSTORE" ]; then
    # Generate client certificate
    keytool -genkey -alias "$CN_CLIENT" \
        -keyalg RSA -keysize 4096 -sigalg SHA512withRSA -keypass "$PASSWORD" \
        -validity 3650 -dname "$DNAME_CLIENT" \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Generate a host certificate signing request
    keytool -certreq -alias "$CN_CLIENT" -ext BC=ca:true \
        -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
        -validity 3650 -file "$CERTS_DIR/$CN_CLIENT.csr" \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Generate signed certificate with the certificate authority
    keytool -gencert -alias ca \
        -validity 3650 -sigalg SHA512withRSA \
        -infile "$CERTS_DIR/$CN_CLIENT.csr" -outfile "$CERTS_DIR/$CN_CLIENT.crt" -rfc \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Import signed certificate into the keystore
    keytool -import -trustcacerts -alias "$CN_CLIENT" \
        -file "$CERTS_DIR/$CN_CLIENT.crt" \
        -keystore "$BUILD_KEYSTORE" -storepass "$PASSWORD"

    # Export private certificate for importing into a browser
    keytool -importkeystore -srcalias "$CN_CLIENT" \
        -srckeystore "$BUILD_KEYSTORE" -srcstorepass "$PASSWORD" \
        -destkeystore "$CLIENT_KEYSTORE" -deststorepass "$PASSWORD" \
        -deststoretype PKCS12
fi
