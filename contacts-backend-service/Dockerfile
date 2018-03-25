FROM openjdk:8
RUN apt-get update \
    && apt-get install -y --no-install-recommends jq \
    && rm -rf /var/lib/apt/lists/*
ARG server_port=8080
EXPOSE $server_port
ENV SERVER_PORT=$server_port
HEALTHCHECK --interval=1s --timeout=1s --start-period=10s CMD bash -c '[[ "$(curl http://localhost:$SERVER_PORT/actuator/health | jq -r .status)" == "UP" ]]'
CMD ["java", "-jar", "/opt/contacts-backend-service.jar"]
COPY build/libs/contacts-backend-service.jar /opt
