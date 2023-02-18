package rs.chat.utils.security.annotations;

import org.springframework.security.test.context.support.WithSecurityContext;
import rs.chat.utils.security.WithMockCustomUserSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
	String username() default "user";

	String password() default "password";

	String[] roles() default "role";
}
