-- 1. 관련 트리거 삭제
DROP TRIGGER IF EXISTS trg_comm_post_search_vector ON public.comm_post;

-- 2. 관련 함수 삭제
DROP FUNCTION IF EXISTS fn_update_comm_post_search_vector();

-- 3. 관련 인덱스 삭제
DROP INDEX idx_comm_post_search_vector;

-- 4. 관련 컬럼 삭제
ALTER TABLE ONLY public.comm_post DROP COLUMN search_vector;