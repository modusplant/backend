package kr.modusplant.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
                                        .email("modusplant@gmail.com") // 이메일 주소
                        )
                        .license( // 라이센스 정보
                                new License().name("MIT License").url("https://github.com/modusplant/backend/blob/develop/LICENSE")
                        )
                )
                .addServersItem(new Server() // 프로덕션 서버 정보 설정
                        .url("https://specified-jaquith-modusplant-0c942371.koyeb.app") // 프로덕션 서버 링크
                        .description("Production Server")  // 프로덕션 서버 설명
                )
                .addServersItem(new Server() // 테스트 서버 정보 설정
                        .url("http://localhost:8080")  // 테스트 서버 링크(http://localhost:8080/swagger-ui/index.html)
                        .description("Test Server")  // 테스트 서버 설명
                );
    }
}