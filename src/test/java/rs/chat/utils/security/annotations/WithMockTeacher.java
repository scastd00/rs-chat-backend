package rs.chat.utils.security.annotations;

import rs.chat.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(username = "teacher", password = "teacher", roles = Constants.TEACHER_ROLE)
public @interface WithMockTeacher {
}
