package rs.chat.security.annotations;

import rs.chat.utils.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(username = "student", password = "student", roles = Constants.STUDENT_ROLE)
public @interface WithMockStudent {
}
