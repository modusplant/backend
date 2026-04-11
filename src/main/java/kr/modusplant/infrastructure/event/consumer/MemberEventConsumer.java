package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.infrastructure.jwt.framework.out.jpa.repository.RefreshTokenJpaRepository;
import kr.modusplant.shared.event.MemberWithdrawalEvent;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;

@Component
public class MemberEventConsumer {
    private final DSLContext dsl;

    private final SiteMemberJpaRepository siteMemberJpaRepository;

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final CommPostLikeJpaRepository commPostLikeJpaRepository;
    private final CommPostBookmarkJpaRepository commPostBookmarkJpaRepository;
    private final CommCommentLikeJpaRepository commCommentLikeJpaRepository;

    public MemberEventConsumer(EventBus eventBus, SiteMemberJpaRepository siteMemberJpaRepository, RefreshTokenJpaRepository refreshTokenJpaRepository, CommPostLikeJpaRepository commPostLikeJpaRepository, CommPostBookmarkJpaRepository commPostBookmarkJpaRepository, CommCommentLikeJpaRepository commCommentLikeJpaRepository, CommPostJpaRepository commPostJpaRepository, CommPostArchiveJpaRepository commPostArchiveJpaRepository, CommPostAbuRepJpaRepository commPostAbuRepJpaRepository, CommCommentJpaRepository commCommentJpaRepository, CommCommentAbuRepJpaRepository commCommentAbuRepJpaRepository, PropBugRepJpaRepository propBugRepJpaRepository, DSLContext dsl) {
        eventBus.subscribe(event -> {
            if (event instanceof MemberWithdrawalEvent memberWithdrawalEvent) {
                deleteAllWithMemberPKAndAlterAllWithMemberFK(memberWithdrawalEvent.getMemberId());
            }
        });
        this.dsl = dsl;
        this.siteMemberJpaRepository = siteMemberJpaRepository;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.commPostLikeJpaRepository = commPostLikeJpaRepository;
        this.commPostBookmarkJpaRepository = commPostBookmarkJpaRepository;
        this.commCommentLikeJpaRepository = commCommentLikeJpaRepository;
    }

    private void deleteAllWithMemberPKAndAlterAllWithMemberFK(UUID memberId) {
        SiteMemberEntity memberEntity = siteMemberJpaRepository.findByUuid(memberId).orElseThrow();
        refreshTokenJpaRepository.deleteByMember(memberEntity);
        commPostLikeJpaRepository.deleteByMemberId(memberId);
        commPostBookmarkJpaRepository.deleteByMemberId(memberId);
        commCommentLikeJpaRepository.deleteByMemberId(memberId);

        dsl.batch(
                dsl.update(COMM_POST_ABU_REP)
                        .setNull(COMM_POST_ABU_REP.MEMB_UUID)
                        .where(COMM_POST_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST)
                        .setNull(COMM_POST.AUTH_MEMB_UUID)
                        .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_COMMENT_ABU_REP)
                        .setNull(COMM_COMMENT_ABU_REP.MEMB_UUID)
                        .where(COMM_COMMENT_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.update(COMM_COMMENT)
                        .setNull(COMM_COMMENT.AUTH_MEMB_UUID)
                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP)
                        .setNull(PROP_BUG_REP.MEMB_UUID)
                        .where(PROP_BUG_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_PROF)
                        .where(SITE_MEMBER_PROF.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_TERM)
                        .where(SITE_MEMBER_TERM.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_AUTH)
                        .where(SITE_MEMBER_AUTH.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER)
                        .where(SITE_MEMBER.UUID.eq(memberId))

        ).execute();
    }
}
