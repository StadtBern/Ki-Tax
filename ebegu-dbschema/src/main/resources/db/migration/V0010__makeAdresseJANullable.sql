
CREATE INDEX IX_fall_besitzer
  ON fall (besitzer_id);

CREATE INDEX IX_fall_verantwortlicher
  ON fall (verantwortlicher_id);

CREATE INDEX IX_benutzer_username
  ON benutzer (username);

ALTER TABLE benutzer
  ADD CONSTRAINT UK_username UNIQUE (username);

ALTER TABLE gesuchsteller_adresse_container MODIFY gesuchsteller_adresseja_id VARCHAR(36) NULL;