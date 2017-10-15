CREATE TABLE IF NOT EXISTS "users" (
  "id" BIGSERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "username" VARCHAR(100) NOT NULL UNIQUE,
  "email" VARCHAR(100) NOT NULL UNIQUE,
  "password" VARCHAR(160) NOT NULL,
  "role" INT NOT NULL
);

CREATE UNIQUE INDEX idx_unique_email ON users (email);
CREATE UNIQUE INDEX idx_unique_username ON users (username);


-- DROP TABLE IF EXISTS "users";
