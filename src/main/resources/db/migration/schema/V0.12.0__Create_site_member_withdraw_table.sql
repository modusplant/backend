create table public.site_member_withdraw(
    uuid uuid NOT NULL PRIMARY KEY,
    reason varchar(40) NOT NULL,
    opinion varchar(600) NULL,
    withdrawn_at timestamp without time zone NOT NULL
);