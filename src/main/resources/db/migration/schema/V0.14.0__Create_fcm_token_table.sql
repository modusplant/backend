CREATE TABLE public.fcm_token (
    id                  SERIAL          NOT NULL,
    memb_uuid           uuid            NOT NULL,
    token               varchar(512)    NOT NULL,
    platform            varchar(10)     NOT NULL,
    created_at          timestamp       NOT NULL,
    last_modified_at    timestamp       NOT NULL
);

ALTER TABLE public.fcm_token ADD CONSTRAINT "PK_FCM_TOKEN" PRIMARY KEY (id);
ALTER TABLE public.fcm_token ADD CONSTRAINT "UK_FCM_TOKEN_TOKEN" UNIQUE (token);