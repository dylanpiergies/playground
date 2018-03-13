FROM openjdk
RUN apt-get update \
    && apt-get install -y --no-install-recommends jq \
    && rm -rf /var/lib/apt/lists/*
COPY build/libs/contacts-backend-service.jar /opt
CMD ["java", "-jar", "/opt/contacts-backend-service.jar", "--spring.profiles.active=dev,docker-dev"]
HEALTHCHECK --interval=1s --timeout=1s --start-period=10s CMD bash -c '[[ "$(curl http://localhost:8081/health | jq -r .status)" == "UP" ]]'
