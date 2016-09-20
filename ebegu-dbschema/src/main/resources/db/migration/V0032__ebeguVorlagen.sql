CREATE TABLE ebegu_vorlage_aud (
	id VARCHAR(36) NOT NULL,
	rev INT NOT NULL,
	revtype TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert DATETIME,
	user_erstellt VARCHAR(36),
	user_mutiert VARCHAR(36),
	gueltig_ab DATE,
	gueltig_bis DATE,
	NAME VARCHAR(255),
	vorlage_id VARCHAR(36),
	PRIMARY KEY (id,rev)
	);

CREATE TABLE ebegu_vorlage (
	id VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME NOT NULL,
	timestamp_mutiert DATETIME NOT NULL,
	user_erstellt VARCHAR(36) NOT NULL,
	user_mutiert VARCHAR(36) NOT NULL,
	version BIGINT NOT NULL,
	gueltig_ab DATE NOT NULL,
	gueltig_bis DATE NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	vorlage_id VARCHAR(36),
	PRIMARY KEY (id)
	);

CREATE TABLE vorlage (
	id VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME NOT NULL,
	timestamp_mutiert DATETIME NOT NULL,
	user_erstellt VARCHAR(36) NOT NULL,
	user_mutiert VARCHAR(36) NOT NULL,
	version BIGINT NOT NULL,
	filename VARCHAR(255) NOT NULL,
	filepfad VARCHAR(255) NOT NULL,
	filesize VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
	);

CREATE TABLE vorlage_aud (
	id VARCHAR(36) NOT NULL,
	rev INT NOT NULL,
	revtype TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert DATETIME,
	user_erstellt VARCHAR(36),
	user_mutiert VARCHAR(36),
	filename VARCHAR(255),
	filepfad VARCHAR(255),
	filesize VARCHAR(255),
	PRIMARY KEY (id,rev)
	);

ALTER TABLE ebegu_vorlage_aud ADD CONSTRAINT FK_ebeguvorlage_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE ebegu_vorlage ADD CONSTRAINT UK_ebeguvorlage_vorlage_id UNIQUE (vorlage_id);

ALTER TABLE ebegu_vorlage ADD CONSTRAINT FK_ebeguvorlage_vorlage_id FOREIGN KEY (vorlage_id) REFERENCES vorlage (id) on delete cascade;;

ALTER TABLE vorlage_aud ADD CONSTRAINT FK_vorlage_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE dokument CHANGE dokument_name filename varchar(255) not null;
ALTER TABLE dokument CHANGE dokument_pfad filepfad varchar(255) not null;
ALTER TABLE dokument CHANGE dokument_size filesize varchar(255) not null;

ALTER TABLE dokument_aud CHANGE dokument_name filename varchar(255);
ALTER TABLE dokument_aud CHANGE dokument_pfad filepfad varchar(255);
ALTER TABLE dokument_aud CHANGE dokument_size filesize varchar(255);

DROP TABLE temp_dokument;

CREATE TABLE download_file (
     id                 VARCHAR(36) NOT NULL,
     timestamp_erstellt DATETIME NOT NULL,
     timestamp_mutiert  DATETIME NOT NULL,
     user_erstellt      VARCHAR(36) NOT NULL,
     user_mutiert       VARCHAR(36) NOT NULL,
     version            BIGINT NOT NULL,
     filename          VARCHAR(255) NOT NULL,
     filepfad          VARCHAR(255) NOT NULL,
     filesize          VARCHAR(255) NOT NULL,
     access_token       VARCHAR(36) NOT NULL,
     ip                 VARCHAR(45) NOT NULL,
     PRIMARY KEY (id)
  );