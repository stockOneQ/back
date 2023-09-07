package umc.stockoneqback.security.annotation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import umc.stockoneqback.global.security.domain.CustomUserDetails;
import umc.stockoneqback.global.security.service.dto.UserDetailsDto;

import java.util.List;

import static umc.stockoneqback.fixture.UserFixture.SAEWOO;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        CustomUserDetails customUserDetails = new CustomUserDetails(new UserDetailsDto(1L, SAEWOO.getLoginId(), SAEWOO.getPassword(), SAEWOO.getName(), List.of(customUser.role())));
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, "", customUserDetails.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}

