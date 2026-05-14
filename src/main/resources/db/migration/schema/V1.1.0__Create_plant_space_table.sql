-- 환경관리
CREATE TABLE public.plant_space (
     id                      BIGINT          NOT NULL GENERATED ALWAYS AS IDENTITY,
     member_uuid             UUID            NOT NULL,
     space_location          VARCHAR(20)     NOT NULL,
     space_location_custom   VARCHAR(100)    NULL,
     is_default              BOOLEAN         NOT NULL DEFAULT FALSE,
     light_intensity         SMALLINT        NULL,
     is_plant_light          BOOLEAN         NULL DEFAULT FALSE,
     light_direction         VARCHAR(20)     NULL,
     light_direction_custom  VARCHAR(100)    NULL,
     ventilation_type        VARCHAR(20)     NULL,
     ventilation_type_custom VARCHAR(100)    NULL,
     humidity                DECIMAL(5, 2)   NULL,
     created_at              timestamp without time zone NOT NULL DEFAULT NOW(),
     updated_at              timestamp without time zone NOT NULL DEFAULT NOW(),
     CONSTRAINT pk_plant_space        PRIMARY KEY (id)
);

-- 인덱스 추가
CREATE UNIQUE INDEX uix_plant_space_member_location
    ON plant_space (member_uuid, space_location)
 WHERE space_location <> 'CUSTOM';

CREATE UNIQUE INDEX uix_plant_space_member_default
    ON plant_space (member_uuid)
    WHERE is_default = true;

CREATE INDEX idx_plant_space_member_id ON plant_space (member_uuid);

-- updated_at 자동 갱신 트리거
CREATE OR REPLACE FUNCTION fn_set_updated_at()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS
'BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END';

CREATE TRIGGER trg_plant_space_updated_at
    BEFORE UPDATE ON plant_space
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

-- 코멘트
COMMENT ON TABLE  plant_space                         IS '식물 공간 환경 정보';
COMMENT ON COLUMN plant_space.id                      IS 'PK';
COMMENT ON COLUMN plant_space.member_uuid             IS 'site_member FK';
COMMENT ON COLUMN plant_space.space_location          IS '공간 위치 코드 (BALCONY:베란다 / OUTDOOR:야외 / INDOOR_WINDOW:실내(창문있음) / INDOOR_NO_WINDOW:실내(창문없음) / CUSTOM:직접입력)';
COMMENT ON COLUMN plant_space.space_location_custom   IS '공간 위치 직접 입력값 — space_location = CUSTOM 일 때만 사용';
COMMENT ON COLUMN plant_space.is_default              IS '기본 공간 여부 (true/false)';
COMMENT ON COLUMN plant_space.light_intensity         IS '빛의 세기 (1:아주약함 / 2:약함 / 3:보통 / 4:강함 / 5:아주강함)';
COMMENT ON COLUMN plant_space.is_plant_light          IS '식물등 사용 여부';
COMMENT ON COLUMN plant_space.light_direction         IS '빛의 방향 코드 (SOUTH:남향 / WEST:서향 / NORTH:북향 / EAST:동향 / CUSTOM:직접입력)';
COMMENT ON COLUMN plant_space.light_direction_custom  IS '빛의 방향 직접 입력값 — light_direction = CUSTOM 일 때만 사용';
COMMENT ON COLUMN plant_space.ventilation_type        IS '환기 방식 코드 (FAN:선풍기 / CIRCULATOR:서큘레이터 / NATURAL:자연환기 / NONE:환기없음 / CUSTOM:직접입력)';
COMMENT ON COLUMN plant_space.ventilation_type_custom IS '환기 방식 직접 입력값 — ventilation_type = CUSTOM 일 때만 사용';
COMMENT ON COLUMN plant_space.humidity                IS '습도 (%)';
COMMENT ON COLUMN plant_space.created_at              IS '생성일시';
COMMENT ON COLUMN plant_space.updated_at              IS '수정일시';