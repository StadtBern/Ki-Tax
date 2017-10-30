# Tabellen InstitutionStammdatenTagesschule erstellen

CREATE TABLE institution_stammdaten_tagesschule (
	id                 VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME    NOT NULL,
	timestamp_mutiert  DATETIME    NOT NULL,
	user_erstellt      VARCHAR(36) NOT NULL,
	user_mutiert       VARCHAR(36) NOT NULL,
	version            BIGINT      NOT NULL,
	vorgaenger_id      VARCHAR(36),
	PRIMARY KEY (id)
);

CREATE TABLE institution_stammdaten_tagesschule_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	PRIMARY KEY (id, rev)
);

#  Modul umbenennen zu ModulTagesschule

RENAME TABLE
		modul TO modul_tagesschule;
RENAME TABLE
		modul_aud TO modul_tagesschule_aud;

RENAME TABLE
		belegung_modul TO belegung_tagesschule_modul_tagesschule;
RENAME TABLE
		belegung_modul_aud TO belegung_tagesschule_modul_tagesschule_aud;

# Loeschen der FKs auf den Modultabellen

ALTER TABLE belegung_tagesschule_modul_tagesschule
	DROP FOREIGN KEY FK_belegung_modul_module_id;
ALTER TABLE belegung_tagesschule_modul_tagesschule
	DROP FOREIGN KEY FK_belegung_modul_belegung_id;
ALTER TABLE belegung_tagesschule_modul_tagesschule_aud
	DROP FOREIGN KEY FK_belegung_modul_aud_revinfo;
ALTER TABLE modul_tagesschule
	DROP FOREIGN KEY FK_modul_institution_stammdaten_id;
ALTER TABLE modul_tagesschule_aud
	DROP FOREIGN KEY FK_modul_aud_revinfo;

ALTER TABLE modul_tagesschule
	DROP COLUMN inst_stammdaten_id;
ALTER TABLE modul_tagesschule_aud
	DROP COLUMN inst_stammdaten_id;

#Anpassung der Columnnames der ModulTabellen
ALTER TABLE belegung_tagesschule_modul_tagesschule
	CHANGE module_id module_tagesschule_id VARCHAR(36);
ALTER TABLE belegung_tagesschule_modul_tagesschule_aud
	CHANGE module_id module_tagesschule_id VARCHAR(36);
ALTER TABLE modul_tagesschule
	CHANGE modulname modul_tagesschule_name VARCHAR(255) NOT NULL;
ALTER TABLE modul_tagesschule_aud
	CHANGE modulname modul_tagesschule_name VARCHAR(255) NOT NULL;

ALTER TABLE belegung_tagesschule_modul_tagesschule
	CHANGE belegung_id belegung_tagesschule_id VARCHAR(36);
ALTER TABLE belegung_tagesschule_modul_tagesschule_aud
	CHANGE belegung_id belegung_tagesschule_id VARCHAR(36);

ALTER TABLE institution_stammdaten_aud
	ADD COLUMN institution_stammdaten_tagesschule_id VARCHAR(36);
ALTER TABLE institution_stammdaten
	ADD COLUMN institution_stammdaten_tagesschule_id VARCHAR(36);
ALTER TABLE modul_tagesschule
	ADD COLUMN institution_stammdaten_tagesschule_id VARCHAR(36);
ALTER TABLE modul_tagesschule_aud
	ADD COLUMN institution_stammdaten_tagesschule_id VARCHAR(36);

# FK Belegung zu ModulTagesschule
ALTER TABLE belegung_tagesschule_modul_tagesschule
	ADD CONSTRAINT FK_belegung_tagesschule_modul_tagesschule_module_tagesschule_id
FOREIGN KEY (module_tagesschule_id)
REFERENCES modul_tagesschule (id);

# FK ModulTagesschule zu Belegung
ALTER TABLE belegung_tagesschule_modul_tagesschule
	ADD CONSTRAINT FK_belegung_tagesschule_modul_tagesschule_belegung_tagesschule_id
FOREIGN KEY (belegung_tagesschule_id)
REFERENCES belegung_tagesschule (id);

# AUD Belegung und Modul Tagesschule
ALTER TABLE belegung_tagesschule_modul_tagesschule_aud
	ADD CONSTRAINT FK_belegung_tagesschule_modul_tagesschule_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE modul_tagesschule_aud
	ADD CONSTRAINT FK_modul_tagesschule_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE modul_tagesschule
	ADD CONSTRAINT FK_modul_tagesschule_institution_stammdaten_tagesschule_id
FOREIGN KEY (institution_stammdaten_tagesschule_id)
REFERENCES institution_stammdaten_tagesschule (id);

ALTER TABLE institution_stammdaten
	ADD CONSTRAINT FK_institution_stammdaten_institution_stammdaten_tagesschule_id
FOREIGN KEY (institution_stammdaten_tagesschule_id)
REFERENCES institution_stammdaten_tagesschule (id);

ALTER TABLE institution_stammdaten_tagesschule_aud
	ADD CONSTRAINT FK_institution_stammdaten_tagesschule_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

