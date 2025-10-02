package kr.modusplant.domains.member.domain.exception;

import kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode;
import kr.modusplant.shared.exception.BusinessException;

<<<<<<<< HEAD:src/main/java/kr/modusplant/domains/member/domain/exception/AlreadyLikedException.java
public class AlreadyLikedException extends BusinessException {
    public AlreadyLikedException() {
        super(MemberErrorCode.ALREADY_LIKED);
========
public class EmptyTargetCommentPathException extends BusinessException {
    public EmptyTargetCommentPathException() {
        super(MemberErrorCode.EMPTY_TARGET_COMMENT_PATH);
>>>>>>>> MP-307-feature-comment-like-api:src/main/java/kr/modusplant/domains/member/domain/exception/EmptyTargetCommentPathException.java
    }
}
