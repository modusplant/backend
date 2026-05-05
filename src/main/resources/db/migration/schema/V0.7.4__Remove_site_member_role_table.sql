ALTER TABLE public.site_member ADD COLUMN role VARCHAR(12);

UPDATE public.site_member sm
SET role = smr.role
FROM public.site_member_role smr
WHERE sm.uuid = smr.uuid;

ALTER TABLE public.site_member ALTER COLUMN role SET NOT NULL;

DROP TABLE site_member_role;