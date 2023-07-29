package umc.stockoneqback.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import umc.stockoneqback.global.security.filters.JwtRequestFilter;
import umc.stockoneqback.global.security.handler.JwtAccessDeniedHandler;
import umc.stockoneqback.global.security.handler.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().antMatchers("/css/**", "/script/**", "image/**", "/fonts/**", "lib/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable();

        http.formLogin().disable();
        http.httpBasic().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http.headers().frameOptions().sameOrigin();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                    .antMatchers("/api/auth/login").permitAll()
                    .antMatchers("/api/user/sign-up/manager").permitAll()
                    .antMatchers("/api/user/sign-up/part-timer").permitAll()
                    .antMatchers("/api/user/sign-up/supervisor").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
