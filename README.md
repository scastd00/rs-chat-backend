# RS Chat

## Description

This application provides a simple chat that allows users to comunicate with each other by using WebSockets.
The chats are divided into:

- Degree chats
  - Where all users of a degree can comunicate with each other.
- Subject chats
  - Where all users of a subject can comunicate with each other.
- Group chats
  - Where all users of a group can comunicate with each other.
- Individual chats
  - Where any two users can comunicate with each other.

Users can send any type of message including text, images, videos, audio, files, etc. All of these messages are
stored in a _bucket_ (provided by the AWS S3 service). They are retrieved from the bucket when a user
connects to any chat.

## Tech stack

- Java 17
- Spring Boot
- Spring Boot Security
- WebSockets (realtime communication)
- MySQL (production)
- JPA
- Lombok (annotations to generate the code)
- H2 database (test)
- Slf4j (logging facade)
- Logback (logging)
- JUnit (testing)
- Mockito (testing)
- Guava (utilities)
- CommonsIO (utilities)
- Gson (JSON utilities)
- S3 (storage)
- Java JWT (authentication)

## UML diagram of the application

![](images/img.png)
