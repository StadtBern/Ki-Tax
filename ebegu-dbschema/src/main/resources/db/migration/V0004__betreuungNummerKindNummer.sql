ALTER TABLE betreuung ADD COLUMN betreuung_nummer INTEGER NOT NULL;
ALTER TABLE betreuung_aud ADD COLUMN betreuung_nummer INTEGER;

ALTER TABLE kind_container ADD COLUMN kind_nummer INTEGER NOT NULL;
ALTER TABLE kind_container ADD COLUMN next_number_betreuung INTEGER NOT NULL;
ALTER TABLE kind_container_aud ADD COLUMN kind_nummer INTEGER;
ALTER TABLE kind_container_aud ADD COLUMN next_number_betreuung INTEGER;

ALTER TABLE gesuch ADD COLUMN next_number_kind INTEGER NOT NULL;

ALTER TABLE gesuch_aud ADD COLUMN next_number_kind INTEGER;


ALTER TABLE betreuung
  ADD CONSTRAINT UK_betreuung_kind_betreuung_nummer UNIQUE (betreuung_nummer, kind_id);

ALTER TABLE kind_container
  ADD CONSTRAINT UK_kindcontainer_gesuch_kind_nummer UNIQUE (kind_nummer, gesuch_id);
