ALTER TABLE benutzer
  ADD COLUMN traegerschaft_id VARCHAR(36);

ALTER TABLE benutzer
  ADD COLUMN institution_id VARCHAR(36);

ALTER TABLE benutzer_aud
  ADD COLUMN traegerschaft_id VARCHAR(36);

ALTER TABLE benutzer_aud
  ADD COLUMN institution_id VARCHAR(36);


ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE benutzer
  ADD CONSTRAINT FK_benutzer_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);
