ALTER TABLE gesuch ADD bemerkungen_pruefungstv VARCHAR(4000) NULL;
ALTER TABLE gesuch_aud ADD bemerkungen_pruefungstv VARCHAR(4000) NULL;

ALTER TABLE gesuch ADD geprueftstv BIT;
ALTER TABLE gesuch_aud ADD geprueftstv BIT;