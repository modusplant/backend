-- 공통코드 테이블
CREATE TABLE public.common_code_group (
   group_code  VARCHAR(30)     NOT NULL,
   group_name  VARCHAR(100)    NOT NULL,
   description VARCHAR(255)    NULL,
   created_at   timestamp without time zone NOT NULL DEFAULT NOW(),
   CONSTRAINT pk_common_code_group PRIMARY KEY (group_code)
);

COMMENT ON TABLE  common_code_group             IS '공통코드 그룹';
COMMENT ON COLUMN common_code_group.group_code  IS '그룹 코드';
COMMENT ON COLUMN common_code_group.group_name  IS '그룹명';
COMMENT ON COLUMN common_code_group.description IS '설명';

CREATE TABLE public.common_code (
     group_code  VARCHAR(30)     NOT NULL,
     code        VARCHAR(20)     NOT NULL,
     label       VARCHAR(100)    NOT NULL,
     sort_order  SMALLINT        NOT NULL DEFAULT 0,
     is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
     created_at   timestamp without time zone NOT NULL DEFAULT NOW(),
     CONSTRAINT pk_common_code   PRIMARY KEY (group_code, code),
     CONSTRAINT fk_common_code_group
         FOREIGN KEY (group_code) REFERENCES public.common_code_group (group_code)
);

COMMENT ON TABLE  common_code                   IS '공통코드';
COMMENT ON COLUMN common_code.group_code        IS '그룹 코드 (FK)';
COMMENT ON COLUMN common_code.code              IS '코드값';
COMMENT ON COLUMN common_code.label             IS 'UI 표시 레이블 (베란다, 남향 등)';
COMMENT ON COLUMN common_code.sort_order        IS '정렬 순서';
COMMENT ON COLUMN common_code.is_active         IS '사용 여부 (비활성화로 소프트 삭제)';
