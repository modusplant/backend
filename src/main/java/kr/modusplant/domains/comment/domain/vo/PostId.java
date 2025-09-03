package kr.modusplant.domains.comment.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {
    private String id;

    public static PostId create(String ulid) {
        return new PostId(ulid);
    }
}
