ALTER TABLE betreuung ADD COLUMN betreuung_nummer INTEGER NOT NULL;
ALTER TABLE betreuung_aud ADD COLUMN betreuung_nummer INTEGER;

ALTER TABLE kind_container ADD COLUMN kind_nummer INTEGER NOT NULL;
ALTER TABLE kind_container ADD COLUMN next_number_betreuung INTEGER NOT NULL;
ALTER TABLE kind_container_aud ADD COLUMN kind_nummer INTEGER;
ALTER TABLE kind_container_aud ADD COLUMN next_number_betreuung INTEGER;

ALTER TABLE gesuch ADD COLUMN next_number_kind INTEGER NOT NULL;

ALTER TABLE gesuch_aud ADD COLUMN next_number_kind INTEGER;


CREATE INDEX IX_betreuung_kind_betreuung_nummer ON betreuung (betreuung_nummer, kind_id);
CREATE INDEX IX_kindcontainer_gesuch_kind_nummer ON kind_container (kind_nummer, gesuch_id);
