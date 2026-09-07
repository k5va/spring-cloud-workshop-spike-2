FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,id=maven-product-service \
    mvn -B -q dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2,id=maven-product-service \
    mvn -B -q package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/product-service-*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=5s --timeout=3s --start-period=30s --retries=5 \
    CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080; printf "GET /actuator/health HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n" >&3; response=$(cat <&3); exec 3<&-; echo "$response" | grep -q "\"status\":\"UP\""' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
