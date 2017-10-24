RENAME TABLE belegung TO belegung_tagesschule;
RENAME TABLE belegung_aud TO belegung_tagesschule_aud;

ALTER TABLE betreuung DROP FOREIGN KEY FK_betreuung_belegung_id;

alter table betreuung change belegung_id belegung_tagesschule_id VARCHAR(36);
alter table betreuung_aud change belegung_id belegung_tagesschule_id VARCHAR(36);

ALTER TABLE betreuung
	ADD CONSTRAINT FK_betreuung_belegung_tagesschule_id
FOREIGN KEY (belegung_tagesschule_id) REFERENCES belegung_tagesschule (id);