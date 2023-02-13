FROM openjdk:17-slim

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
COPY env ./env
COPY scripts/run_in_docker.sh ./

COPY --from=tarampampam/curl:7.78.0 /bin/curl /bin/curl

EXPOSE 4040
ENTRYPOINT ["./run_in_docker.sh"]
