ALTER TABLE gesuch add timestamp_verfuegt DATETIME;
ALTER TABLE gesuch_aud add timestamp_verfuegt DATETIME;

ALTER TABLE gesuch ADD gueltig BIT NOT NULL DEFAULT FALSE;
ALTER TABLE gesuch_aud ADD gueltig BIT;

ALTER TABLE betreuung ADD gueltig BIT NOT NULL DEFAULT FALSE;
ALTER TABLE betreuung_aud ADD gueltig BIT;

CREATE INDEX IX_kind_geburtsdatum ON kind (geburtsdatum);