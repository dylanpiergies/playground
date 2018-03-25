FROM openjdk:8-alpine
RUN apk add curl jq --no-cache
ARG server_port=8080
EXPOSE $server_port
ENV SERVER_PORT=$server_port
HEALTHCHECK --interval=1s --timeout=1s --start-period=10s CMD /bin/sh -c '[[ "$(curl http://localhost:$SERVER_PORT/actuator/health | jq -r .status)" == "UP" ]]'
CMD ["java", "-jar", "/opt/contacts-backend-service.jar"]
COPY build/libs/contacts-backend-service.jar /opt/
