CREATE TABLE berechtigung_history (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(36)  NOT NULL,
	user_mutiert       VARCHAR(36)  NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	gesperrt           BIT          NOT NULL,
	gueltig_ab         DATE         NOT NULL,
	gueltig_bis        DATE         NOT NULL,
	role               VARCHAR(255) NOT NULL,
	username	       VARCHAR(255)  NOT NULL,
	institution_id     VARCHAR(36),
	traegerschaft_id   VARCHAR(36),
	geloescht           BIT          NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE berechtigung_history_aud (
	id                 VARCHAR(36) NOT NULL,
	rev                INTEGER     NOT NULL,
	revtype            TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert  DATETIME,
	user_erstellt      VARCHAR(36),
	user_mutiert       VARCHAR(36),
	vorgaenger_id      VARCHAR(36),
	gesperrt           BIT,
	gueltig_ab         DATE,
	gueltig_bis        DATE,
	role               VARCHAR(255),
	username           VARCHAR(255),
	institution_id     VARCHAR(36),
	traegerschaft_id   VARCHAR(36),
	geloescht           BIT,
	PRIMARY KEY (id, rev)
);

ALTER TABLE berechtigung_history
	ADD CONSTRAINT FK_berechtigung_history_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);

ALTER TABLE berechtigung_history
	ADD CONSTRAINT FK_berechtigung_history_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE berechtigung_history_aud
	ADD CONSTRAINT FK_berechtigung_history_aud_rev
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

INSERT INTO berechtigung_history (
	SELECT
		UUID(),
		timestamp_erstellt,
		timestamp_mutiert,
		user_erstellt,
		user_mutiert,
		version,
		NULL,
		false,
		gueltig_ab,
		gueltig_bis,
		role,
		(select username from benutzer where id = benutzer_id),
		institution_id,
		traegerschaft_id,
		false
	FROM berechtigung);