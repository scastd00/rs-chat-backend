package rs.chat.utils.security.annotations;

import rs.chat.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockCustomUser(username = "admin", password = "admin", roles = Constants.ADMIN_ROLE)
public @interface WithMockAdmin {
}
