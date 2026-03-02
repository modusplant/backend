package kr.modusplant.infrastructure.swear.service;

import kr.modusplant.infrastructure.swear.common.util.SwearEntityTestUtils;
import kr.modusplant.infrastructure.swear.persistence.jpa.repository.SwearJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SwearServiceTest implements SwearEntityTestUtils {
    private final SwearJpaRepository repository = Mockito.mock(SwearJpaRepository.class);
    private SwearService service;

    @BeforeEach
    public void setUp() {
        given(repository.findAll()).willReturn(testSwearEntityList);
        service = new SwearService(repository);
        service.init();
    }

    @Test
    @DisplayName("욕설이 섞인 문자열을 필터링하여 반환함")
    public void testFilterText_givenStringWithSwear_willReturnFilteredString() {
        // given & when
        String result = service.filterSwear("애1미야");

        // then
        assertThat(result).isEqualTo("***야");
    }

    @Test
    @DisplayName("빈 문자열을 그대로 반환함")
    public void testFilterText_givenBlankString_willReturnString() {
        // given & when
        String result = service.filterSwear(" ");

        // then
        assertThat(result).isEqualTo(" ");
    }

    @Test
    @DisplayName("null 을 그대로 반환함")
    public void testFilterText_givenNull_willReturnNull() {
        // given & when
        String result = service.filterSwear(null);

        // then
        assertThat(result).isEqualTo(null);
    }

    @Test
    @DisplayName("문자열에 욕설이 있으면 true 를 반환함")
    public void testIsSwearContained_givenStringWithSwear_willReturnTrue() {
        // given & when
        boolean result = service.isSwearContained("애미야");

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("빈 문자열이면 false 를 반환함")
    public void testIsSwearContained_givenEmptyString_willReturnFalse() {
        // given & when
        boolean result = service.isSwearContained(" ");

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("null 이면 false 를 반환함")
    public void testIsSwearContained_givenNull_willReturnFalse() {
        // given & when
        boolean result = service.isSwearContained(null);

        // then
        assertThat(result).isEqualTo(false);
    }
}
