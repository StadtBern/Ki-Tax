ALTER TABLE familiensituation
  ADD COLUMN aenderung_per DATE DEFAULT NULL;

ALTER TABLE familiensituation_aud
  ADD COLUMN aenderung_per DATE DEFAULT NULL;
