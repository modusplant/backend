package kr.modusplant.domains.comment.framework.out.persistence.jpa.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO: 게시글 담당자의 PostEntity 로 대체할 것
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostEntity {

    @Id
    private String ulid;

    public static PostEntity create(String ulid) {
        return new PostEntity(ulid);
    }
}
