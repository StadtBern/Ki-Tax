ALTER TABLE gesuch DROP FOREIGN KEY FK_gesuch_mutationsdaten_id;
ALTER TABLE mutationsdaten_aud DROP FOREIGN KEY FK_mutationsdaten_aud_revinfo;

ALTER TABLE gesuch DROP mutationsdaten_id;
ALTER TABLE gesuch_aud DROP mutationsdaten_id;

DROP TABLE mutationsdaten;
DROP TABLE mutationsdaten_aud;
