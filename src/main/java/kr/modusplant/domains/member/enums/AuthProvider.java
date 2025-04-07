<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/enums/AuthProvider.java
package kr.modusplant.domains.member.enums;
========
package kr.modusplant.api.crud.member.enums;
>>>>>>>> 8263f89 (MP-92 :goal_net: Catch: 응답 구조 상태코드 수정):src/main/java/kr/modusplant/api/crud/member/enums/AuthProvider.java

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("Google"),
    KAKAO("Kakao"),
    BASIC("Basic");

    private final String value;

    AuthProvider(String value) {
        this.value = value;
    }
}