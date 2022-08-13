# Class structure

<!-- TOC -->

* [Controllers](#controllers)
* [Services](#services)
* [Repositories](#repositories)

<!-- TOC -->

# Controllers

### It must have at least the following code:

```java
package rs.chat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {
    private final Service aService;
}
```

IMPORTANT NOTE: `final` keyword in controllers is mandatory since Dependency Injection
is done automatically by Spring Boot. If it is not `final` an error will be thrown.

Annotations provide:

- `@Slf4j`: logger for the class
- `@RestController`: Specifies that this class is a controller to Spring Boot
- `@RequiredArgsConstructor`: Creates a default constructor with the necessary arguments

# Services

### It must have at least the following code:

```java
package rs.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class Service {
    private final Repository aRepository;
}
```

IMPORTANT NOTE: `final` keyword in services is mandatory since Dependency Injection
is done automatically by Spring Boot. If it is not `final` an error will be thrown.

The annotations provide:

- `@Service`: Specifies that this class is a Service to Spring Boot.
- `@RequiredArgsConstructor`: Creates a default constructor with the necessary arguments
- `@Transactional`: Specifies that this class will make transactions to database.
- `@Slf4j`: logger for the class

# Repositories

### It must have at least the following code:

```java
package rs.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository extends JpaRepository<EntityClass, EntityIDClass> {
}
```

This type of interfaces provide access to database. Since it extends from `JpaRepository`
default operations can be executed directly, but others must be specified as a method signature (Spring
knows what SQL query must be executed).

