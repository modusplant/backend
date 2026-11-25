ALTER TABLE public.swear RENAME TO prohibited_word;
ALTER TABLE public.prohibited_word RENAME CONSTRAINT "PK_SWEAR" TO "PK_PROHIBITED_WORD";
ALTER TABLE public.prohibited_word RENAME CONSTRAINT "UK_SWEAR" TO "UK_PROHIBITED_WORD";
ALTER SEQUENCE public.swear_id_seq RENAME TO prohibited_word_id_seq;

ALTER TABLE public.not_swear RENAME TO allowed_word;
ALTER TABLE public.allowed_word RENAME CONSTRAINT "PK_NOT_SWEAR" TO "PK_ALLOWED_WORD";
ALTER INDEX public.uix_not_swear_word RENAME TO uix_allowed_word_word;
ALTER SEQUENCE public.not_swear_id_seq RENAME TO allowed_word_id_seq;

COMMENT ON TABLE  public.allowed_word      IS '허용된 말';
COMMENT ON COLUMN public.allowed_word.word IS '금지된 말을 포함하지만 금지된 말이 아닌 말';
