CREATE TABLE IF NOT EXISTS "users" (
  "id" BIGSERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "username" VARCHAR NOT NULL UNIQUE,
  "email" VARCHAR NOT NULL UNIQUE,
  "password" VARCHAR NOT NULL
);

CREATE UNIQUE INDEX idx_unique_email ON users (email);
CREATE UNIQUE INDEX idx_unique_username ON users (username);



-- DROP TABLE IF EXISTS "users";
