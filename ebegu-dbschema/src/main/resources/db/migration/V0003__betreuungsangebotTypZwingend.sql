UPDATE institution_stammdaten SET betreuungsangebot_typ = 'KITA' WHERE betreuungsangebot_typ IS NULL;
ALTER TABLE institution_stammdaten MODIFY betreuungsangebot_typ VARCHAR(255) NOT NULL;