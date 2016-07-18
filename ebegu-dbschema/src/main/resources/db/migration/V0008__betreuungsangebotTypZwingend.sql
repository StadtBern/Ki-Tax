UPDATE institution_stammdaten SET betreuungsangebot_typ = 'KITA' WHERE betreuungsangebot_typ IS NULL;
ALTER TABLE institution_stammdaten MODIFY betreuungsangebot_typ VARCHAR(255) NOT NULL;

UPDATE betreuungspensum SET pensum = 0 WHERE pensum IS NULL;
ALTER TABLE betreuungspensum MODIFY pensum INTEGER NOT NULL;

UPDATE erwerbspensum SET pensum = 0 WHERE pensum IS NULL;
ALTER TABLE erwerbspensum MODIFY pensum INTEGER NOT NULL;

UPDATE pensum_fachstelle SET pensum = 0 WHERE pensum IS NULL;
ALTER TABLE pensum_fachstelle MODIFY pensum INTEGER NOT NULL;