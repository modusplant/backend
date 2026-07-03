UPDATE public.comm_post
SET content = (
    SELECT jsonb_agg(
                   CASE
                       WHEN item->>'type' = 'text'
                           THEN item - 'filename'
                       ELSE item
                       END
                   ORDER BY (item->>'order')::int
           )
    FROM jsonb_array_elements(content::jsonb) AS item
)
WHERE content IS NOT NULL
  AND content::jsonb @> '[{"type": "text"}]';