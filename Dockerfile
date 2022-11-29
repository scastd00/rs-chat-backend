FROM openjdk:17-slim

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
COPY env ./env
COPY run_in_docker.sh ./

EXPOSE 4040-4041
ENTRYPOINT ["./run_in_docker.sh"]
