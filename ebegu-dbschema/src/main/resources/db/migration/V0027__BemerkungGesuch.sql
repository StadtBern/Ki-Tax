ALTER TABLE gesuch ADD COLUMN bemerkungen VARCHAR(1000);
ALTER TABLE gesuch_aud ADD COLUMN bemerkungen VARCHAR(1000);

ALTER TABLE familiensituation DROP bemerkungen;
ALTER TABLE familiensituation_aud DROP bemerkungen;

ALTER TABLE kind DROP bemerkungen;
ALTER TABLE kind_aud DROP bemerkungen;

ALTER TABLE betreuung DROP bemerkungen;
ALTER TABLE betreuung_aud DROP bemerkungen;