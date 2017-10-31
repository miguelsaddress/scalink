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

INSERT INTO users (name, username, email, password, role) VALUES ('Admin', 'admin', 'admin@example.com', '$2a$12$3FK.ZpGkRUHd5XV4Io9L3eVl4BMYeyubdyyONjLAXo9AYIi/FL422', 0);
INSERT INTO users (name, username, email, password, role) VALUES ('Miguel', 'miguel', 'miguel@example.com', '$2a$12$3FK.ZpGkRUHd5XV4Io9L3eVl4BMYeyubdyyONjLAXo9AYIi/FL422', 1);

-- DROP TABLE IF EXISTS "users";
