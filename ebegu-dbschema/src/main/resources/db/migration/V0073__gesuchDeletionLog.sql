CREATE TABLE gesuch_deletion_log (
	id                VARCHAR(36)  NOT NULL,
	version           BIGINT       NOT NULL,
	timestamp_deleted DATETIME     NOT NULL,
	user_deleted      VARCHAR(36)  NOT NULL,
	cause             VARCHAR(255) NOT NULL,
	fall_nummer       BIGINT       NOT NULL,
	gesuch_id         VARCHAR(36)  NOT NULL,
	geburtsdatum      DATE,
	nachname          VARCHAR(255),
	vorname           VARCHAR(255),
	PRIMARY KEY (id)
);