package kr.modusplant.domains.communication.tip.app.http.like.response;

public record TipLikeResponse(int likeCount, boolean liked) {
    public static TipLikeResponse of(int likeCount, boolean liked) {
        return new TipLikeResponse(likeCount, liked);
    }
}