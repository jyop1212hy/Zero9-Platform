-- DB 컬럼 추가(기존 회원이 있다면 provider 기본값을 LOCAL로 두는 게 안전)
ALTER TABLE users
  ADD COLUMN provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
  ADD COLUMN provider_id VARCHAR(255) NULL,
  ADD COLUMN nickname VARCHAR(100) NULL,
  ADD COLUMN profile_image_url VARCHAR(500) NULL;


-- 유니크 인덱스(이게 있어야 중복 가입/동시성을 DB 레벨에서 막음)
-- provider_id가 NULL인 로컬 유저가 많으면 DB에 따라 유니크 인덱스 동작이 애매할 수 있습니다
-- (MySQL은 NULL 여러 개 허용).
-- 그래서 보통 provider_id는 소셜 유저만 채우고, 로컬은 NULL 유지해도 OK.
CREATE UNIQUE INDEX ux_users_provider_provider_id
    ON users (provider, provider_id);