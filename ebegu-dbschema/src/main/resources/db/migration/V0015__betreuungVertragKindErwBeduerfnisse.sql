ALTER TABLE betreuung
  ADD COLUMN vertrag BIT NOT NULL DEFAULT FALSE;

ALTER TABLE betreuung_aud
  ADD COLUMN vertrag BIT;

ALTER TABLE betreuung
  ADD COLUMN erweiterte_beduerfnisse BIT NOT NULL DEFAULT FALSE;

ALTER TABLE betreuung_aud
  ADD COLUMN erweiterte_beduerfnisse BIT;
