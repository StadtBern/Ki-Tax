CREATE TABLE berechtigung (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(36)  NOT NULL,
	user_mutiert       VARCHAR(36)  NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	gueltig_ab         DATE         NOT NULL,
	gueltig_bis        DATE         NOT NULL,
	role               VARCHAR(255) NOT NULL,
	benutzer_id        VARCHAR(36)  NOT NULL,
	institution_id     VARCHAR(36),
	traegerschaft_id   VARCHAR(36),
	PRIMARY KEY (id)
);

CREATE TABLE berechtigung_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	gueltig_ab         DATE,
	gueltig_bis        DATE,
	role               VARCHAR(255),
	benutzer_id        VARCHAR(36),
	institution_id     VARCHAR(36),
	traegerschaft_id   VARCHAR(36),
	PRIMARY KEY (id, rev)
);

ALTER TABLE benutzer ADD current_berechtigung_id VARCHAR(36);
ALTER TABLE benutzer_aud ADD current_berechtigung_id VARCHAR(36);

ALTER TABLE benutzer
	ADD CONSTRAINT UK_current_berechtigung_id UNIQUE (current_berechtigung_id);

ALTER TABLE berechtigung
	ADD CONSTRAINT FK_Berechtigung_benutzer_id
FOREIGN KEY (benutzer_id)
REFERENCES benutzer (id);

ALTER TABLE berechtigung
	ADD CONSTRAINT FK_Berechtigung_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);

ALTER TABLE berechtigung
	ADD CONSTRAINT FK_Berechtigung_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE berechtigung_aud
	ADD CONSTRAINT FK_Berechtigung_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);
