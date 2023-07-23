package umc.stockoneqback.security.roles;

import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
@BeforeTransaction
public @interface WithMockCustomUser {
    String role() default "SUPERVISOR";
}