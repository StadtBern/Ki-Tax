ALTER TABLE betreuungsmitteilung_pensum ADD COLUMN monatliche_mittagessen INTEGER NOT NULL DEFAULT 0;
ALTER TABLE betreuungsmitteilung_pensum_aud ADD COLUMN monatliche_mittagessen INTEGER;

ALTER TABLE betreuungspensum ADD COLUMN monatliche_mittagessen INTEGER NOT NULL DEFAULT 0;
ALTER TABLE betreuungspensum_aud ADD COLUMN monatliche_mittagessen INTEGER;