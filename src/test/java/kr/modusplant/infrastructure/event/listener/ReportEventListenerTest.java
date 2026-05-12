package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.aws.service.S3FileService;
import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.jpa.repository.*;
import kr.modusplant.shared.event.ProposalOrBugReportRemoveEvent;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.shared.event.common.util.CommentAbuseReportEventTestUtils.testCommentAbuseReportEvent;
import static kr.modusplant.shared.event.common.util.PostAbuseReportEventTestUtils.testPostAbuseReportEvent;
import static kr.modusplant.shared.event.common.util.ProposalOrBugReportEventTestUtils.testProposalOrBugReportEvent;
import static kr.modusplant.shared.event.common.util.ProposalOrBugReportRemoveEventTestUtils.testProposalOrBugReportRemoveEvent;
import static kr.modusplant.shared.persistence.common.util.constant.CommPostConstant.TEST_COMM_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class ReportEventListenerTest implements SiteMemberEntityTestUtils {
    private final String emptyReportId = "empty_report_id";

    private final MockDataProvider mockDataProvider = (MockExecuteContext mockExecuteContext) -> {
        String sql = mockExecuteContext.sql().toLowerCase();

        // 이미지 경로 조회 (select) 모킹
        if (sql.contains("select") && sql.contains("jsonb_array_elements")) {
            DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
            Result<Record1<String>> result = dsl.newResult(DSL.field("src", String.class));

            // 바인딩된 reportId 값을 기준으로 이미지 없음 케이스 분기
            boolean isEmptyCase = false;
            if (mockExecuteContext.bindings() != null) {
                for (Object binding : mockExecuteContext.bindings()) {
                    if (emptyReportId.equals(binding)) {
                        isEmptyCase = true;
                        break;
                    }
                }
            }

            if (!isEmptyCase) {
                result.add(dsl.newRecord(DSL.field("src", String.class)).values(TEST_REPORT_PROPOSAL_OR_BUG_IMAGE_PATH_1));
            }
            return new MockResult[] { new MockResult(result.size(), result) };
        }

        // 데이터 삽입 및 삭제 (insert, delete) 모킹
        if (sql.contains("insert") || sql.contains("delete")) {
            return new MockResult[] { new MockResult(1) };
        }

        return new MockResult[0];
    };

    private final MockConnection mockConnection = new MockConnection(mockDataProvider);
    private final DSLContext dslContext = DSL.using(mockConnection, SQLDialect.POSTGRES);

    private final S3FileService s3FileService = mock(S3FileService.class);
    private final SiteMemberJpaRepository memberJpaRepository = mock(SiteMemberJpaRepository.class);
    private final CommPostJpaRepository postJpaRepository = mock(CommPostJpaRepository.class);
    private final CommCommentJpaRepository commentJpaRepository = mock(CommCommentJpaRepository.class);
    private final PropBugRepJpaRepository propBugRepJpaRepository = mock(PropBugRepJpaRepository.class);
    private final CommPostAbuRepJpaRepository postAbuRepJpaRepository = mock(CommPostAbuRepJpaRepository.class);
    private final CommCommentAbuRepJpaRepository commentAbuRepJpaRepository = mock(CommCommentAbuRepJpaRepository.class);

    private final ReportEventListener reportEventListener = new ReportEventListener(
            dslContext, s3FileService, memberJpaRepository, postJpaRepository,
            commentJpaRepository, propBugRepJpaRepository, postAbuRepJpaRepository, commentAbuRepJpaRepository
    );

    @Test
    @DisplayName("존재하는 회원에 대해 ProposalOrBugReportEvent 실행 시 건의 및 버그 제보 저장 성공")
    void testProposalOrBugReportEvent_givenValidData_willSaveReport() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));

        // when
        reportEventListener.handleProposalOrBugReportEvent(testProposalOrBugReportEvent);

        // then
        verify(propBugRepJpaRepository, times(1)).save(any(PropBugRepEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 ProposalOrBugReportEvent 실행 실패")
    void testProposalOrBugReportEvent_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportEventListener.handleProposalOrBugReportEvent(testProposalOrBugReportEvent));

        // then
        verify(propBugRepJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미지가 존재하는 리포트에 대해 ProposalOrBugReportRemoveEvent 실행 시 S3 삭제 및 아카이빙 수행")
    void testProposalOrBugReportRemoveEvent_givenImagesExist_willDeleteS3AndProcessArchive() {
        // given & when
        reportEventListener.handleProposalOrBugReportRemoveEvent(testProposalOrBugReportRemoveEvent);

        // then
        verify(s3FileService, times(1)).deleteFiles(anyList());
    }

    @Test
    @DisplayName("이미지가 없는 리포트에 대해 ProposalOrBugReportRemoveEvent 실행 시 S3 삭제 생략 후 아카이빙 수행")
    void testProposalOrBugReportRemoveEvent_givenImagesEmpty_willProcessArchiveOnly() {
        // given
        ProposalOrBugReportRemoveEvent event = mock(ProposalOrBugReportRemoveEvent.class);
        given(event.getReportId()).willReturn(emptyReportId); // MockDataProvider에서 빈 목록을 반환하도록 처리됨

        // when
        reportEventListener.handleProposalOrBugReportRemoveEvent(event);

        // then
        verify(s3FileService, never()).deleteFiles(anyList());
    }

    @Test
    @DisplayName("존재하는 회원 및 게시글에 대해 PostAbuseReportEvent 실행 시 신고 저장 성공")
    void testPostAbuseReportEvent_givenValidData_willSaveReport() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        CommPostEntity mockPost = mock(CommPostEntity.class);
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_COMM_POST_ULID)).willReturn(Optional.of(mockPost));

        // when
        reportEventListener.handlePostAbuseReportEvent(testPostAbuseReportEvent);

        // then
        verify(postAbuRepJpaRepository, times(1)).save(any(CommPostAbuRepEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 PostAbuseReportEvent 실행 실패")
    void testPostAbuseReportEvent_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportEventListener.handlePostAbuseReportEvent(testPostAbuseReportEvent));

        // then
        verify(postAbuRepJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 게시글로 인해 PostAbuseReportEvent 실행 실패")
    void testPostAbuseReportEvent_givenNotFoundPost_willThrowException() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(postJpaRepository.findByUlid(TEST_POST_ULID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportEventListener.handlePostAbuseReportEvent(testPostAbuseReportEvent));

        // then
        verify(postAbuRepJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하는 회원 및 댓글에 대해 CommentAbuseReportEvent 실행 시 신고 저장 성공")
    void testCommentAbuseReportEvent_givenValidData_willSaveReport() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        CommCommentEntity commentEntity = mock(CommCommentEntity.class);

        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(commentJpaRepository.findById(any(CommCommentId.class))).willReturn(Optional.of(commentEntity));

        // when
        reportEventListener.handleCommentAbuseReportEvent(testCommentAbuseReportEvent);

        // then
        verify(commentAbuRepJpaRepository, times(1)).save(any(CommCommentAbuRepEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 인해 CommentAbuseReportEvent 실행 실패")
    void testCommentAbuseReportEvent_givenNotFoundMember_willThrowException() {
        // given
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportEventListener.handleCommentAbuseReportEvent(testCommentAbuseReportEvent));

        // then
        verify(commentAbuRepJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 댓글로 인해 CommentAbuseReportEvent 실행 실패")
    void testCommentAbuseReportEvent_givenNotFoundComment_willThrowException() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntity();
        given(memberJpaRepository.findByUuid(MEMBER_BASIC_USER_UUID)).willReturn(Optional.of(memberEntity));
        given(commentJpaRepository.findById(any(CommCommentId.class))).willReturn(Optional.empty());

        // when
        assertThrows(NoSuchElementException.class, () -> reportEventListener.handleCommentAbuseReportEvent(testCommentAbuseReportEvent));

        // then
        verify(commentAbuRepJpaRepository, never()).save(any());
    }
}