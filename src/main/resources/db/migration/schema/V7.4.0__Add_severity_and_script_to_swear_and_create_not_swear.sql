ALTER TABLE public.swear ADD COLUMN severity SMALLINT;
ALTER TABLE public.swear ADD COLUMN script VARCHAR(12);

UPDATE public.swear SET severity = 3 WHERE type IN ('SEXUAL', 'FAMILY');
UPDATE public.swear SET severity = 2 WHERE type = 'GENERAL';
UPDATE public.swear SET script = 'HANGUL';

ALTER TABLE public.swear ALTER COLUMN severity SET NOT NULL;
ALTER TABLE public.swear ALTER COLUMN script SET NOT NULL;

CREATE TABLE public.not_swear (
    id         BIGINT                      NOT NULL GENERATED ALWAYS AS IDENTITY,
    word       VARCHAR(20)                 NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "PK_NOT_SWEAR" PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uix_not_swear_word ON public.not_swear (word);

COMMENT ON TABLE  public.not_swear            IS '비속어를 포함하지만 비속어가 아닌 일반 단어';
COMMENT ON COLUMN public.not_swear.id         IS '식별자';
COMMENT ON COLUMN public.not_swear.word       IS '비속어가 아닌 단어';
COMMENT ON COLUMN public.not_swear.created_at IS '생성일';
COMMENT ON COLUMN public.swear.severity       IS '심각도';
COMMENT ON COLUMN public.swear.script         IS '단어의 문자 집합';
