FROM openjdk:17-slim
EXPOSE 4040-4041
COPY target/rs-chat-backend-0.0.1.jar /app.jar
COPY jars/opentelemetry-javaagent-1.20.2.jar /opentelemetry-javaagent.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
