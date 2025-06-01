package kr.modusplant.domains.communication.qna.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class QnaPostRepositoryTest implements QnaPostEntityTestUtils, QnaCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final QnaPostRepository qnaPostRepository;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    QnaPostRepositoryTest(QnaPostRepository qnaPostRepository, QnaCategoryRepository qnaCategoryRepository, SiteMemberRepository siteMemberRepository) {
        this.qnaPostRepository = qnaPostRepository;
        this.qnaCategoryRepository = qnaCategoryRepository;
        this.siteMemberRepository = siteMemberRepository;
    }

    private QnaCategoryEntity testQnaCategory;
    private SiteMemberEntity testSiteMember;

    @BeforeEach
    void setUp() {
        testQnaCategory = qnaCategoryRepository.save(testQnaCategoryEntity);
        testSiteMember = siteMemberRepository.save(createMemberBasicUserEntity());
    }

    @Test
    @DisplayName("ULID로 팁 게시글 찾기")
    void findByUlidTest() {
        // given
        QnaPostEntity qnaPostEntity = createQnaPostEntityBuilder()
                .group(testQnaCategory)
                .authMember(testSiteMember)
                .createMember(testSiteMember)
                .build();

        // when
        qnaPostRepository.save(qnaPostEntity);
        System.out.println(qnaPostEntity);

        // then
        assertThat(qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow()).isEqualTo(qnaPostEntity);
    }

    @Test
    @DisplayName("전체 팁 게시글 찾기(최신순)")
    void findAllByOrderByCreatedAtDescTest() {
        // given
        List<QnaPostEntity> qnaPosts = IntStream.range(0, 10)
                .mapToObj(i -> createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        qnaPostRepository.saveAll(qnaPosts);

        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<QnaPostEntity> result = qnaPostRepository.findAllByOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(4);

        List<QnaPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findByIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        List<QnaPostEntity> qnaPosts = IntStream.range(0, 5)
                .mapToObj(i -> createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        qnaPosts.get(0).updateIsDeleted(true);
        qnaPostRepository.saveAll(qnaPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<QnaPostEntity> result = qnaPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(4); // 삭제된 1건 제외
        assertThat(result.getContent().stream().allMatch(post -> !post.getIsDeleted())).isTrue();

        List<QnaPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("카테고리로 삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findByGroupAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        QnaCategoryEntity testOtherGroup = qnaCategoryRepository.save(
                QnaCategoryEntity.builder().order(2).category("기타").build()
        );
        List<QnaPostEntity> qnaPosts = IntStream.range(0, 5)
                .mapToObj(i -> createQnaPostEntityBuilder()
                        .group(i % 2 == 0 ? testQnaCategory : testOtherGroup)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
                ).collect(Collectors.toList());
        qnaPosts.get(0).updateIsDeleted(true);
        qnaPostRepository.saveAll(qnaPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<QnaPostEntity> result = qnaPostRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(testQnaCategory, pageable);

        // then
        // i = 0, 2, 4 → testQnaCategory로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getGroup().equals(testQnaCategory) && !post.getIsDeleted())).isTrue();

        List<QnaPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }

    @Test
    @DisplayName("인가 회원으로 삭제되지 않은 모든 팁 게시글 찾기(최신순)")
    void findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDescTest() {
        // given
        SiteMemberEntity testSiteMember2 = siteMemberRepository.save(createMemberGoogleUserEntity());
        List<QnaPostEntity> qnaPosts = IntStream.range(0, 5)
                .mapToObj(i -> createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .createMember(i % 2 == 0 ? testSiteMember : testSiteMember2)
                        .build()
                ).collect(Collectors.toList());
        qnaPosts.get(0).updateIsDeleted(true);
        qnaPostRepository.saveAll(qnaPosts);

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<QnaPostEntity> result = qnaPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(testSiteMember,pageable);

        // then
        // i = 0, 2, 4 → testSiteMember로 생성됨 (0번은 삭제됨)
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().stream().allMatch(post -> post.getAuthMember().equals(testSiteMember) && !post.getIsDeleted())).isTrue();

        List<QnaPostEntity> content = result.getContent();
        for (int i = 0; i < content.size() - 1; i++) {
            assertThat(content.get(i).getCreatedAt())
                    .isAfterOrEqualTo(content.get(i + 1).getCreatedAt());
        }
    }


    @Test
    @DisplayName("createdAt으로 회원 찾기")
    void findByCreatedAtTest() {
        // when
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(qnaPostRepository.findByCreatedAt(qnaPostEntity.getCreatedAt()).getFirst()).isEqualTo(qnaPostEntity);
    }

    @Test
    @DisplayName("updatedAt으로 회원 찾기")
    void findByUpdatedAtTest() {
        // when
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(qnaPostRepository.findByUpdatedAt(qnaPostEntity.getUpdatedAt()).getFirst()).isEqualTo(qnaPostEntity);
    }

    @Test
    @DisplayName("ULID로 팁 게시글 삭제")
    void deleteByUlidTest() {
        // given
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        String ulid = qnaPostEntity.getUlid();

        // when
        qnaPostRepository.deleteByUlid(ulid);

        // then
        assertThat(qnaPostRepository.findByUlid(ulid)).isEmpty();
    }

    @Test
    @DisplayName("ULID로 팁 게시글 확인")
    void existsByUlidTest() {
        // when
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );

        // then
        assertThat(qnaPostRepository.existsByUlid(qnaPostEntity.getUlid())).isEqualTo(true);
    }

    @Test
    @DisplayName("ulid로 삭제되지 않은 게시글 조회")
    void findByUlidAndIsDeletedFalseTest() {
        // given
        QnaPostEntity qnaPostEntity1 = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build()
        );
        QnaPostEntity qnaPostEntity2 = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .isDeleted(true)
                        .build()
        );

        // when
        Optional<QnaPostEntity> found = qnaPostRepository.findByUlidAndIsDeletedFalse(qnaPostEntity1.getUlid());
        Optional<QnaPostEntity> notFound = qnaPostRepository.findByUlidAndIsDeletedFalse(qnaPostEntity2.getUlid());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUlid()).isEqualTo(qnaPostEntity1.getUlid());
        assertThat(notFound).isEmpty();

    }

    @Test
    @DisplayName("제목+본문 검색어로 게시글 목록 찾기")
    void searchByTitleOrContentTest() {
        // given
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .build());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<QnaPostEntity> result1 = qnaPostRepository.searchByTitleOrContent("물",pageable);
        Page<QnaPostEntity> result2 = qnaPostRepository.searchByTitleOrContent("this",pageable);
        Page<QnaPostEntity> result3 = qnaPostRepository.searchByTitleOrContent("erd",pageable);

        // then
        assertThat(result1.getTotalElements()).isEqualTo(1);
        assertThat(result2.getTotalElements()).isEqualTo(1);
        assertThat(result3.getTotalElements()).isEqualTo(0);
        assertThat(result1.getContent().get(0).getContent().get(1).has("src")).isEqualTo(true);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 크면 조회수 수정 성공")
    void updateViewCountSuccessTest() {
        // given
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = qnaPostRepository.updateViewCount(qnaPostEntity.getUlid(),20L);

        // then
        assertThat(updatedCount).isEqualTo(1);
        entityManager.clear();
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(20L);
    }

    @Test
    @DisplayName("현재 조회수보다 업데이트할 조회수가 더 작거나 같으면 조회수 수정 실패")
    void updateViewCountFailTest() {
        // given
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(testQnaCategory)
                        .authMember(testSiteMember)
                        .createMember(testSiteMember)
                        .viewCount(10L)
                        .build()
        );

        // when
        int updatedCount = qnaPostRepository.updateViewCount(qnaPostEntity.getUlid(),5L);

        // then
        assertThat(updatedCount).isEqualTo(0);
        entityManager.clear();
        QnaPostEntity result = qnaPostRepository.findByUlid(qnaPostEntity.getUlid()).orElseThrow();
        assertThat(result.getViewCount()).isEqualTo(10L);
    }

}