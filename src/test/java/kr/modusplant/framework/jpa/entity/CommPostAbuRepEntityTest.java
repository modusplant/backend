package kr.modusplant.framework.jpa.entity;

import kr.modusplant.framework.jpa.entity.common.util.CommPostAbuRepEntityTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.member.common.util.domain.vo.MemberIdTestUtils.testMemberId;
import static kr.modusplant.shared.persistence.common.util.constant.ReportConstant.TEST_REPORT_ULID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CommPostAbuRepEntityTest implements CommPostAbuRepEntityTestUtils {

    private CommPostAbuRepEntity testCommPostAbuRepEntity;

    @BeforeEach
    public void beforeEach() {
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = createCommPrimaryCategoryEntityWithId();
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        testCommPostAbuRepEntity =
                createCommPostAbuRepEntityBuilderWithUlid()
                        .member(memberEntity)
                        .post(
                                createCommPostEntityBuilder()
                                        .primaryCategory(commPrimaryCategoryEntity)
                                        .secondaryCategory(
                                                createCommSecondaryCategoryEntityBuilderWithId()
                                                        .primaryCategory(commPrimaryCategoryEntity)
                                                        .build()
                                        )
                                        .authMember(memberEntity)
                                        .build()
                        ).build();
    }

    @Test
    @DisplayName("getETagSource를 통해 ETag 소스 반환")
    void testGetETagSource_givenNothing_willReturnETagSource() {
        assertEquals(testCommPostAbuRepEntity.getETagSource(), TEST_REPORT_ULID + "-" + null);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testCommPostAbuRepEntity, testCommPostAbuRepEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testCommPostAbuRepEntity, testMemberId);
    }

    @Test
    @DisplayName("같은 타입의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfEqualType_willReturnFalse() {
        assertEquals(testCommPostAbuRepEntity, CommPostAbuRepEntity.builder().commPostAbuRep(testCommPostAbuRepEntity).build());
    }

    @Test
    @DisplayName("같은 객체에 대한 hashcode 동일성 보장")
    void testHashCode_givenSameObject_willReturnSameHashCode() {
        assertEquals(testCommPostAbuRepEntity.hashCode(), testCommPostAbuRepEntity.hashCode());
    }
}