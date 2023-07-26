package umc.stockoneqback.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import umc.stockoneqback.auth.utils.JwtTokenProvider;
import umc.stockoneqback.global.annotation.ExtractPayloadArgumentResolver;
import umc.stockoneqback.global.annotation.ExtractTokenArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /*
        개발 환경에서의 크로스 도메인 이슈를 해결하기 위한 코드이다.
        운영 환경에 배포할 경우에는 15~18행을 주석 처리한다.
        크로스 도메인 이슈: 브라우저에서 다른 도메인으로 URL 요청을 하는 경우 나타나는 보안문제
        */

//        registry.addMapping("/api/**").allowCredentials(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExtractTokenArgumentResolver(jwtTokenProvider));
        resolvers.add(new ExtractPayloadArgumentResolver(jwtTokenProvider));
    }
}
