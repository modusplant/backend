package kr.modusplant.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info() // API 문서 정보 설정
                        .title("ModusPlant Backend API") // API 제목
                        .version("0.0.1") // API 버전
                        .description("ModusPlant Backend API 명세서") // API 설명
                        .termsOfService("https://modusplant.vercel.app/terms") // API 약관 링크
                        .contact( // 연락처 정보
                                new Contact()
                                        .name("ModusPlant Support") // 지원 팀 이름
                                        .url("https://modusplant.vercel.app/support") // 지원 페이지 링크
                                        .email("modusplant@gmail.com") // 이메일
                        )
                        .license( // 라이센스 정보
                                new License().name("MIT License").url("https://github.com/modusplant/backend/blob/develop/LICENSE")
                        )
                )
                .addServersItem(new Server() // 운영 서버 정보 설정
                        .url("http://43.203.86.156/") // 운영 서버 링크
                        .description("Prod Server")
                )
                .addServersItem(new Server() // 개발 서버 정보 설정
                        .url("https://kormap.ddnsfree.com") // 개발 서버 링크
                        .description("Dev Server")  // 개발 서버 설명
                )
                .addServersItem(new Server() // 테스트 서버 정보 설정
                        .url("http://localhost:8080")  // 테스트 서버 링크(http://localhost:8080/swagger-ui/index.html)
                        .description("Test Local Server")  // 테스트 서버 설명
                )
                .components(new Components()
                        .addParameters(
                                "authCookie", new Parameter()
                                        .in("cookie")
                                        .name("Authorization")
                                        .description("보안 조치가 적용된 토큰 전용 쿠키")
                                        .required(false)
                                        .schema(new StringSchema())));
    }
}