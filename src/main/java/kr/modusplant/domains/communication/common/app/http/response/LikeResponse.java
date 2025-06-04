package kr.modusplant.domains.communication.common.app.http.response;

public record LikeResponse(int likeCount, boolean liked) {
    public static LikeResponse of(int likeCount, boolean liked) {
        return new LikeResponse(likeCount, liked);
    }
}