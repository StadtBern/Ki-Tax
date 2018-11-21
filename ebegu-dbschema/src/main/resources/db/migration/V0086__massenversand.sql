CREATE TABLE massenversand (
	id                 VARCHAR(36)  NOT NULL,
	timestamp_erstellt DATETIME     NOT NULL,
	timestamp_mutiert  DATETIME     NOT NULL,
	user_erstellt      VARCHAR(255) NOT NULL,
	user_mutiert       VARCHAR(255) NOT NULL,
	version            BIGINT       NOT NULL,
	vorgaenger_id      VARCHAR(36),
	einstellungen      VARCHAR(255) NOT NULL,
	text               VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE massenversand_gesuch (
	massenversand_id VARCHAR(36) NOT NULL,
	gesuche_id       VARCHAR(36) NOT NULL
);

ALTER TABLE massenversand_gesuch
	ADD CONSTRAINT FK_massenversand_gesuch_gesuch_id
FOREIGN KEY (gesuche_id)
REFERENCES gesuch (id);

ALTER TABLE massenversand_gesuch
	ADD CONSTRAINT FK_massenversand_gesuch_massenversand_id
FOREIGN KEY (massenversand_id)
REFERENCES massenversand (id);