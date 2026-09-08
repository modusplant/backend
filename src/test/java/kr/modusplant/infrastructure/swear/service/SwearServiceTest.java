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
    @DisplayName("욕설 포함 문자열로 문자열 반환")
    public void testFilterSwear_givenStringWithSwear_willReturnString() {
        // given & when
        String result = service.filterSwear("애1미야");

        // then
        assertThat(result).isEqualTo("***야");
    }

    @Test
    @DisplayName("빈 문자열로 문자열 반환")
    public void testFilterSwear_givenBlankString_willReturnString() {
        // given & when
        String result = service.filterSwear(" ");

        // then
        assertThat(result).isEqualTo(" ");
    }

    @Test
    @DisplayName("null로 null 반환")
    public void testFilterSwear_givenNull_willReturnNull() {
        // given & when
        String result = service.filterSwear(null);

        // then
        assertThat(result).isEqualTo(null);
    }

    @Test
    @DisplayName("욕설 포함 문자열로 참 반환")
    public void testIsSwearContained_givenStringWithSwear_willReturnTrue() {
        // given & when
        boolean result = service.isSwearContained("애미야");

        // then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("빈 문자열로 거짓 반환")
    public void testIsSwearContained_givenEmptyString_willReturnFalse() {
        // given & when
        boolean result = service.isSwearContained(" ");

        // then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("null로 거짓 반환")
    public void testIsSwearContained_givenNull_willReturnFalse() {
        // given & when
        boolean result = service.isSwearContained(null);

        // then
        assertThat(result).isEqualTo(false);
    }
}
