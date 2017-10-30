CREATE TABLE IF NOT EXISTS "link" (
  "id" BIGSERIAL NOT NULL PRIMARY KEY,
  "url" VARCHAR(255) NOT NULL,
  "title" VARCHAR(255) NOT NULL,
  "description" VARCHAR(500) NOT NULL,
  "user_id" BIGSERIAL NOT NULL
);

CREATE UNIQUE INDEX idx_unique_url_per_user ON link (url, user_id);
CREATE UNIQUE INDEX idx_unique_title_per_user ON link (title, user_id);


-- DROP TABLE IF EXISTS "link";
