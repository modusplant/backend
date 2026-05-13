-- 식물관리
CREATE TABLE public.plant_info (
    id           uuid        NOT NULL DEFAULT uuid_generate_v4(),
    member_uuid  uuid        NOT NULL,
    plant_name   text        NOT NULL,
    description  text,
    plant_id     int4        NOT NULL,
    space_id     bigint      NULL,
    watering_cycle_days int2,
    grown_since  date,
    created_at   timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at   timestamp without time zone NOT NULL DEFAULT NOW(),
    CONSTRAINT plant_info_pkey PRIMARY KEY (id),
    CONSTRAINT fk_plant_info_member     FOREIGN KEY (member_uuid) REFERENCES site_member(uuid) ON DELETE CASCADE,
    CONSTRAINT fk_plant_info_plant FOREIGN KEY (plant_id) REFERENCES public.plant(id),
    CONSTRAINT fk_plant_info_space    FOREIGN KEY (space_id)  REFERENCES public.plant_space(id) ON DELETE SET NULL
);

-- 인덱스 추가
CREATE INDEX idx_plant_info_member_uuid ON public.plant_info (member_uuid);
CREATE INDEX idx_plant_info_space_id ON public.plant_info (space_id);

-- updated_at 자동 갱신 트리거
CREATE TRIGGER trg_plant_info_updated_at
    BEFORE UPDATE ON plant_info
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TABLE public.plant_info_photo (
    id             uuid        NOT NULL DEFAULT uuid_generate_v4(),
    plant_info_id  uuid        NOT NULL,
    file_path      text        NOT NULL,
    sort_order     int2        NOT NULL DEFAULT 1,
    created_at   timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at   timestamp without time zone NOT NULL DEFAULT NOW(),
    CONSTRAINT plant_info_photo_pkey PRIMARY KEY (id),
    CONSTRAINT fk_photo_plant_info FOREIGN KEY (plant_info_id) REFERENCES public.plant_info(id) ON DELETE CASCADE
);

-- 인덱스 추가
CREATE INDEX idx_plant_info_photo_plant_info_id ON public.plant_info_photo (plant_info_id);

-- updated_at 자동 갱신 트리거
CREATE TRIGGER trg_plant_info_photo_updated_at
    BEFORE UPDATE ON plant_info_photo
    FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();
