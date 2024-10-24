CREATE TABLE song (
      id SERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      artist VARCHAR(255) NOT NULL,
      album VARCHAR(255),
      length VARCHAR(10),
      resource_id BIGINT,
      year VARCHAR(4)
);