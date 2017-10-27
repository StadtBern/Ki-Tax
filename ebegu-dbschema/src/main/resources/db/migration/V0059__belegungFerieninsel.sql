-- Belegung umbenennen zu BelegungTagesschule

RENAME TABLE belegung TO belegung_tagesschule;
RENAME TABLE belegung_aud TO belegung_tagesschule_aud;

ALTER TABLE betreuung DROP FOREIGN KEY FK_betreuung_belegung_id;

ALTER TABLE betreuung CHANGE belegung_id belegung_tagesschule_id VARCHAR(36);
ALTER TABLE betreuung_aud CHANGE belegung_id belegung_tagesschule_id VARCHAR(36);

ALTER TABLE betreuung
	ADD CONSTRAINT FK_betreuung_belegung_tagesschule_id
FOREIGN KEY (belegung_tagesschule_id) REFERENCES belegung_tagesschule (id);

-- Neue Belegung fuer Ferieninseln: BelegungFerieninsel

CREATE TABLE belegung_ferieninsel_tag (
	id                 VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME    NOT NULL,
	timestamp_mutiert  DATETIME    NOT NULL,
	user_erstellt      VARCHAR(36) NOT NULL,
	user_mutiert       VARCHAR(36) NOT NULL,
	version            BIGINT      NOT NULL,
	vorgaenger_id      VARCHAR(36),
	tag                DATE        NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE belegung_ferieninsel_tag_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	tag                DATE,
	PRIMARY KEY (id, rev)
);

CREATE TABLE belegung_ferieninsel (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(36)  NOT NULL,
	user_mutiert       VARCHAR(36)  NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	ferienname         VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE belegung_ferieninsel_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	ferienname         VARCHAR(255),
	PRIMARY KEY (id, rev)
);

CREATE TABLE belegung_ferieninsel_belegung_ferieninsel_tag (
	belegung_ferieninsel_id VARCHAR(36) NOT NULL,
	tage_id                 VARCHAR(36) NOT NULL
);

CREATE TABLE belegung_ferieninsel_belegung_ferieninsel_tag_aud (
	rev                     INTEGER     NOT NULL,
	belegung_ferieninsel_id VARCHAR(36) NOT NULL,
	tage_id                 VARCHAR(36) NOT NULL,
	revtype                 TINYINT,
	PRIMARY KEY (rev, belegung_ferieninsel_id, tage_id)
);

ALTER TABLE betreuung ADD belegung_ferieninsel_id VARCHAR(36);
ALTER TABLE betreuung_aud ADD belegung_ferieninsel_id VARCHAR(36);

ALTER TABLE belegung_ferieninsel_aud
	ADD CONSTRAINT FK_belegung_ferieninsel_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE belegung_ferieninsel_belegung_ferieninsel_tag_aud
	ADD CONSTRAINT FK_belegung_ferieninsel_belegung_ferieninsel_tag_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE belegung_ferieninsel_tag_aud
	ADD CONSTRAINT FK_belegung_ferieninsel_tag_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE belegung_ferieninsel_belegung_ferieninsel_tag
	ADD CONSTRAINT FK_belegung_ferieninsel_belegung_ferieninsel_tag_tage
FOREIGN KEY (tage_id)
REFERENCES belegung_ferieninsel_tag (id);

ALTER TABLE belegung_ferieninsel_belegung_ferieninsel_tag
	ADD CONSTRAINT FK_belegung_ferieninsel_belegung_ferieninsel_tag_ferieninsel
FOREIGN KEY (belegung_ferieninsel_id)
REFERENCES belegung_ferieninsel (id);

ALTER TABLE betreuung
	ADD CONSTRAINT FK_betreuung_belegung_ferieninsel_id
FOREIGN KEY (belegung_ferieninsel_id)
REFERENCES belegung_ferieninsel (id);