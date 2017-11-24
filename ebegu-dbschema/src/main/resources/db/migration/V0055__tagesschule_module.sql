CREATE TABLE belegung (
	id                 VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME    NOT NULL,
	timestamp_mutiert  DATETIME    NOT NULL,
	user_erstellt      VARCHAR(36) NOT NULL,
	user_mutiert       VARCHAR(36) NOT NULL,
	version            BIGINT      NOT NULL,
	vorgaenger_id      VARCHAR(36),
	PRIMARY KEY (id)
);

CREATE TABLE belegung_aud (
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

CREATE TABLE belegung_modul (
	belegung_id VARCHAR(36) NOT NULL,
	module_id   VARCHAR(36) NOT NULL,
	PRIMARY KEY (belegung_id, module_id)
);

CREATE TABLE belegung_modul_aud (
	rev         INTEGER     NOT NULL,
	belegung_id VARCHAR(36),
	module_id   VARCHAR(36),
	revtype     TINYINT,
	PRIMARY KEY (rev, belegung_id, module_id)
);

CREATE TABLE modul (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(36)  NOT NULL,
	user_mutiert       VARCHAR(36)  NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	modulname          VARCHAR(255) NOT NULL,
	wochentag          VARCHAR(255) NOT NULL,
	zeit_bis           TIME         NOT NULL,
	zeit_von           TIME         NOT NULL,
	inst_stammdaten_id VARCHAR(36)  NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE modul_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	modulname          VARCHAR(255),
	wochentag          VARCHAR(255),
	zeit_bis           TIME,
	zeit_von           TIME,
	inst_stammdaten_id VARCHAR(36),
	PRIMARY KEY (id, rev)
);

ALTER TABLE betreuung ADD belegung_id VARCHAR(36);
ALTER TABLE betreuung_aud ADD belegung_id VARCHAR(36);


ALTER TABLE belegung_aud
	ADD CONSTRAINT FK_belegung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE belegung_modul
	ADD CONSTRAINT FK_belegung_modul_module_id
FOREIGN KEY (module_id)
REFERENCES modul (id);

ALTER TABLE belegung_modul
	ADD CONSTRAINT FK_belegung_modul_belegung_id
FOREIGN KEY (belegung_id)
REFERENCES belegung (id);

ALTER TABLE belegung_modul_aud
	ADD CONSTRAINT FK_belegung_modul_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE modul
	ADD CONSTRAINT FK_modul_institution_stammdaten_id
FOREIGN KEY (inst_stammdaten_id)
REFERENCES institution_stammdaten (id);

ALTER TABLE modul_aud
	ADD CONSTRAINT FK_modul_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuung
	ADD CONSTRAINT FK_betreuung_belegung_id
FOREIGN KEY (belegung_id)
REFERENCES belegung (id);