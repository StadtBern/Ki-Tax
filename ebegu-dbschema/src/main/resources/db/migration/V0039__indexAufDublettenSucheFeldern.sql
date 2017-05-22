ALTER TABLE gesuch add datum_verfuegt DATE;
ALTER TABLE gesuch_aud add datum_verfuegt DATE;

ALTER TABLE gesuch ADD gueltig BIT NOT NULL DEFAULT FALSE;
ALTER TABLE gesuch_aud ADD gueltig BIT;

CREATE INDEX IX_kind_geburtsdatum ON kind (geburtsdatum);
# CREATE INDEX IX_kind_nachname ON kind (nachname);
# CREATE INDEX IX_kind_vorname ON kind (vorname);