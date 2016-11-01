ALTER TABLE familiensituation
  ADD COLUMN aenderung_per DATE DEFAULT NULL;

ALTER TABLE familiensituation_aud
  ADD COLUMN aenderung_per DATE;


ALTER TABLE gesuch
  ADD COLUMN familiensituation_erstgesuch_id VARCHAR(36) DEFAULT NULL;

ALTER TABLE gesuch_aud
  ADD COLUMN familiensituation_erstgesuch_id VARCHAR(36);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_familiensituation_erstgesuch_id
FOREIGN KEY (familiensituation_erstgesuch_id)
REFERENCES familiensituation (id);
