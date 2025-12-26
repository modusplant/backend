package kr.modusplant.domains.comment.usecase.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CommentPageResponse<T>{
    private List<T> commentList;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public void ApplyOneIndexBasedPage() {
        this.page += 1;
    }
}
