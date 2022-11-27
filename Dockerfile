FROM openjdk:17-slim
EXPOSE 4040-4041
COPY target/rs-chat-backend-0.0.1.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
