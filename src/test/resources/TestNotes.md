# Spring Boot Tests

In order to do the tests of the application, several libraries/frameworks are used:

* JUnit 5
* Mockito
* AssertJ
* Spring Boot Test
* Spring Mock MVC
* Spring Data JPA Test

## Syntax

### Repository

```java
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class RepositoryTest {
  @Autowired
  private Repository underTest;

  @Test
  void test() {
    // ...
  }
}
```

If we need other dependencies, we can use the `@Autowired` annotation. This allows to inject
the dependencies in the test class.

To perform the actual checks, we can use the `assertThat` method from the `Assertions` class
from the `org.assertj.core.api` package.

### Service

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceTest {
  @Mock
  private Repository repository;

  private Service underTest;

  @Test
  void test() {
    // ...
  }
}
```

The `@ExtendWith` annotation is used to extend the test class with the `MockitoExtension` class.
This allows to use the `@Mock` annotation, which allows to initialize that dependencies.

To perform the actual checks, we use several methods from the `Mockito` and `Assertions` classes:

* `verify` method from the `Mockito` class, which allows to verify that a method was called some number of times (specified as a parameter).
* `assertThat` method from the `Assertions` class, which allows to perform checks on the result of the method call.

If we need to check the parameters passed to the method, we can use the `ArgumentCaptor` class for some specific class:
`ArgumentCaptor<SomeClass> argumentCaptor = ArgumentCaptor.forClass(SomeClass.class);`. Then, we should use the `capture` method inside
the parameter list of the method when called: `verify(repository).someMethod(argumentCaptor.capture());`. Finally, we can use the `getValue` method
to get the value of the parameter that the method received: `SomeClass value = argumentCaptor.getValue();`. This allows to perform checks on the value of the parameter
with the `assertThat` method.

### Controller

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(Controller.class)
class ControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private Service service;
  // More mocked service dependencies

  @Test
  void test() /* throws Exception */ {
    // ...
  }
}
```

The `@WebMvcTest` annotation is used to test the controller. It allows to inject the `MockMvc` class,
which allows to perform HTTP requests to the controller. The `@MockBean` annotation is used to mock the dependencies of the controller.

To be able to perform the HTTP requests, we need to use the `perform` method from the `MockMvc` class.
This method returns a `ResultActions` object, which allows to perform checks on the response. The `andExpect` method
enables us to perform checks on the response. The `status` method from the `MockMvcResultMatchers` class allows to check the status code
of the response. If the request method is `POST`, we can use the `content` method to add the body of the request.

Then the response can be checked with the methods provided by the `Assertions` class.

If an exception should be thrown by the controller there are several ways to configure it:

1. `given(service.method(any(ParameterClass.class))).willThrow(new Exception());`
2. `willThrow(new Exception()).given(service).method();`

The first one is used when the method **DOES NOT** return void, we can use the `any` method from the `ArgumentMatchers` class.
The second one is used when the method **DOES** return void.
