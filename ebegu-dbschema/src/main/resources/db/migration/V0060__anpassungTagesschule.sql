-- Modul umbenennen zu ModulTagesschule

RENAME TABLE modul TO modul_tagesschule;
RENAME TABLE modul_aud TO modul_tagesschule_aud;

RENAME TABLE belegung_modul TO belegung_modul_tagesschule;
RENAME TABLE belegung_modul_aud TO belegung_modul_tagesschule_aud;

ALTER TABLE belegung_modul_tagesschule DROP FOREIGN KEY FK_belegung_modul_module_id;
ALTER TABLE belegung_modul_tagesschule DROP FOREIGN KEY FK_belegung_modul_belegung_id;
ALTER TABLE belegung_modul_tagesschule_aud DROP FOREIGN KEY FK_belegung_modul_aud_revinfo;
ALTER TABLE modul_tagesschule DROP FOREIGN KEY FK_modul_institution_stammdaten_id;
ALTER TABLE modul_tagesschule_aud DROP FOREIGN KEY FK_modul_aud_revinfo;

ALTER TABLE belegung_modul_tagesschule CHANGE module_id module_tagesschule_id VARCHAR(36);
ALTER TABLE belegung_modul_tagesschule_aud CHANGE module_id module_tagesschule_id VARCHAR(36);

ALTER TABLE modul_tagesschule CHANGE modulname modul_tagesschule_name VARCHAR(255) NOT NULL;
ALTER TABLE modul_tagesschule_aud CHANGE modulname modul_tagesschule_name VARCHAR(255) NOT NULL;

ALTER TABLE belegung_modul_tagesschule
	ADD CONSTRAINT FK_belegung_modul_tagesschule_module_tagesschule_id
FOREIGN KEY (module_tagesschule_id)
REFERENCES modul_tagesschule (id);

ALTER TABLE belegung_modul_tagesschule
	ADD CONSTRAINT FK_belegung_modul_tagesschule_belegung_id
FOREIGN KEY (belegung_id)
REFERENCES belegung (id);

ALTER TABLE belegung_modul_tagesschule_aud
	ADD CONSTRAINT FK_belegung_modul_tagesschule_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE modul_tagesschule
	ADD CONSTRAINT FK_modul_tagesschule_institution_stammdaten_id
FOREIGN KEY (inst_stammdaten_id)
REFERENCES institution_stammdaten (id);

ALTER TABLE modul_tagesschule_aud
	ADD CONSTRAINT FK_modul_tagesschule_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);