ALTER TABLE betreuung ADD COLUMN grund_ablehnung VARCHAR(1000);
ALTER TABLE betreuung_aud ADD COLUMN grund_ablehnung VARCHAR(1000);

ALTER TABLE betreuung ADD COLUMN datum_ablehnung DATE;
ALTER TABLE betreuung_aud ADD COLUMN datum_ablehnung DATE;

ALTER TABLE betreuung ADD COLUMN datum_bestaetigung DATE;
ALTER TABLE betreuung_aud ADD COLUMN datum_bestaetigung DATE;
