CREATE TABLE ferieninsel_zeitraum (
	id                 VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME    NOT NULL,
	timestamp_mutiert  DATETIME    NOT NULL,
	user_erstellt      VARCHAR(36) NOT NULL,
	user_mutiert       VARCHAR(36) NOT NULL,
	version            BIGINT      NOT NULL,
	vorgaenger_id      VARCHAR(36),
	gueltig_ab         DATE        NOT NULL,
	gueltig_bis        DATE        NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE ferieninsel_zeitraum_aud (
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
	PRIMARY KEY (id, rev)
);

CREATE TABLE ferieninsel_stammdaten (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(36)  NOT NULL,
	user_mutiert       VARCHAR(36)  NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	anmeldeschluss     DATE         NOT NULL,
	ferienname         VARCHAR(255) NOT NULL,
	gesuchsperiode_id  VARCHAR(36)  NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE ferieninsel_stammdaten_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	anmeldeschluss     DATE,
	ferienname         VARCHAR(255),
	gesuchsperiode_id  VARCHAR(36),
	PRIMARY KEY (id, rev)
);

CREATE TABLE ferieninsel_stammdaten_ferieninsel_zeitraum (
	ferieninsel_stammdaten_id VARCHAR(36) NOT NULL,
	zeitraum_list_id          VARCHAR(36) NOT NULL,
	PRIMARY KEY (ferieninsel_stammdaten_id, zeitraum_list_id)
);

CREATE TABLE ferieninsel_stammdaten_ferieninsel_zeitraum_aud (
	rev                       INTEGER     NOT NULL,
	ferieninsel_stammdaten_id VARCHAR(36) NOT NULL,
	zeitraum_list_id          VARCHAR(36) NOT NULL,
	revtype                   TINYINT,
	PRIMARY KEY (rev, ferieninsel_stammdaten_id, zeitraum_list_id)
);

ALTER TABLE ferieninsel_stammdaten_aud
	ADD CONSTRAINT FK_ferieninsel_stammdaten_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE ferieninsel_stammdaten_ferieninsel_zeitraum_aud
	ADD CONSTRAINT FK_ferieninsel_stammdaten_ferieninsel_zeitraum_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE ferieninsel_zeitraum_aud
	ADD CONSTRAINT FK_ferieninsel_zeitraum_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE ferieninsel_stammdaten
	ADD CONSTRAINT FK_ferieninsel_stammdaten_gesuchsperiodeId
FOREIGN KEY (gesuchsperiode_id)
REFERENCES gesuchsperiode (id);

ALTER TABLE ferieninsel_stammdaten_ferieninsel_zeitraum
	ADD CONSTRAINT UK_ferieninsel_stammdaten_ferieninsel_zeitraum UNIQUE (zeitraum_list_id);

ALTER TABLE ferieninsel_stammdaten_ferieninsel_zeitraum
	ADD CONSTRAINT FK_ferieninsel_stammdaten_ferieninsel_zeitraum_zeitraumListId
FOREIGN KEY (zeitraum_list_id)
REFERENCES ferieninsel_zeitraum (id);

ALTER TABLE ferieninsel_stammdaten_ferieninsel_zeitraum
	ADD CONSTRAINT FK_ferieninsel_stammdaten_ferieninsel_zeitraum_stammdatenId
FOREIGN KEY (ferieninsel_stammdaten_id)
REFERENCES ferieninsel_stammdaten (id);
