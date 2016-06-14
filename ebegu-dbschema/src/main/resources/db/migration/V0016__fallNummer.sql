ALTER TABLE fall ADD fall_nummer INTEGER;

ALTER TABLE fall_aud ADD fall_nummer INTEGER;

ALTER TABLE fall ADD CONSTRAINT UK_fall_nummer UNIQUE (fall_nummer);

CREATE INDEX IX_fall_fall_nummer ON fall (fall_nummer);

ALTER TABLE fall MODIFY fall_nummer INTEGER AUTO_INCREMENT NOT NULL;
