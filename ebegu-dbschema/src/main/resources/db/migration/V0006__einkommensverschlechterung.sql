-- ---------------------------------------------------------------------------------------------------------------------
-- Create new tables for einkommensverschlechterung
-- ---------------------------------------------------------------------------------------------------------------------
CREATE TABLE einkommensverschlechterung (
	id VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME NOT NULL,
	timestamp_mutiert DATETIME NOT NULL,
	user_erstellt VARCHAR(36) NOT NULL,
	user_mutiert VARCHAR(36) NOT NULL,
	version BIGINT NOT NULL,
	bruttovermoegen DECIMAL(19, 2),
	erhaltene_alimente DECIMAL(19, 2),
	ersatzeinkommen DECIMAL(19, 2),
	familienzulage DECIMAL(19, 2),
	geleistete_alimente DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
	schulden DECIMAL(19, 2),
	selbstaendig BIT NOT NULL,
	steuererklaerung_ausgefuellt BIT NOT NULL,
	steuerveranlagung_erhalten BIT NOT NULL,
	nettolohn_apr DECIMAL(19, 2),
	nettolohn_aug DECIMAL(19, 2),
	nettolohn_dez DECIMAL(19, 2),
	nettolohn_feb DECIMAL(19, 2),
	nettolohn_jan DECIMAL(19, 2),
	nettolohn_jul DECIMAL(19, 2),
	nettolohn_jun DECIMAL(19, 2),
	nettolohn_mai DECIMAL(19, 2),
	nettolohn_mrz DECIMAL(19, 2),
	nettolohn_nov DECIMAL(19, 2),
	nettolohn_okt DECIMAL(19, 2),
	nettolohn_sep DECIMAL(19, 2),
	PRIMARY KEY (id)
	);

CREATE TABLE einkommensverschlechterung_aud (
	id VARCHAR(36) NOT NULL,
	rev INT NOT NULL,
	revtype TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert DATETIME,
	user_erstellt VARCHAR(36),
	user_mutiert VARCHAR(36),
	bruttovermoegen DECIMAL(19, 2),
	erhaltene_alimente DECIMAL(19, 2),
	ersatzeinkommen DECIMAL(19, 2),
	familienzulage DECIMAL(19, 2),
	geleistete_alimente DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
	geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
	schulden DECIMAL(19, 2),
	selbstaendig BIT,
	steuererklaerung_ausgefuellt BIT,
	steuerveranlagung_erhalten BIT,
	nettolohn_apr DECIMAL(19, 2),
	nettolohn_aug DECIMAL(19, 2),
	nettolohn_dez DECIMAL(19, 2),
	nettolohn_feb DECIMAL(19, 2),
	nettolohn_jan DECIMAL(19, 2),
	nettolohn_jul DECIMAL(19, 2),
	nettolohn_jun DECIMAL(19, 2),
	nettolohn_mai DECIMAL(19, 2),
	nettolohn_mrz DECIMAL(19, 2),
	nettolohn_nov DECIMAL(19, 2),
	nettolohn_okt DECIMAL(19, 2),
	nettolohn_sep DECIMAL(19, 2),
	PRIMARY KEY (
		id
		,rev
		)
	);

CREATE TABLE einkommensverschlechterung_container_aud (
	id VARCHAR(36) NOT NULL,
	rev INT NOT NULL,
	revtype TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert DATETIME,
	user_erstellt VARCHAR(36),
	user_mutiert VARCHAR(36),
	ekvgsbasis_jahr_plus1_id VARCHAR(36),
	ekvgsbasis_jahr_plus2_id VARCHAR(36),
	ekvjabasis_jahr_plus1_id VARCHAR(36),
	ekvjabasis_jahr_plus2_id VARCHAR(36),
	gesuchsteller_id VARCHAR(36),
	PRIMARY KEY (
		id
		,rev
		)
	);

CREATE TABLE einkommensverschlechterung_info_aud (
	id VARCHAR(36) NOT NULL,
	rev INT NOT NULL,
	revtype TINYINT,
	timestamp_erstellt DATETIME,
	timestamp_mutiert DATETIME,
	user_erstellt VARCHAR(36),
	user_mutiert VARCHAR(36),
	einkommensverschlechterung BIT,
	ekv_fuer_basis_jahr_plus1 BIT,
	ekv_fuer_basis_jahr_plus2 BIT,
	grund_fuer_basis_jahr_plus1 VARCHAR(255),
	grund_fuer_basis_jahr_plus2 VARCHAR(255),
	stichtag_fuer_basis_jahr_plus1 date,
	stichtag_fuer_basis_jahr_plus2 date,
	PRIMARY KEY (
		id
		,rev
		)
	);

CREATE TABLE einkommensverschlechterung_container (
	id VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME NOT NULL,
	timestamp_mutiert DATETIME NOT NULL,
	user_erstellt VARCHAR(36) NOT NULL,
	user_mutiert VARCHAR(36) NOT NULL,
	version BIGINT NOT NULL,
	ekvgsbasis_jahr_plus1_id VARCHAR(36),
	ekvgsbasis_jahr_plus2_id VARCHAR(36),
	ekvjabasis_jahr_plus1_id VARCHAR(36),
	ekvjabasis_jahr_plus2_id VARCHAR(36),
	gesuchsteller_id VARCHAR(36) NOT NULL,
	PRIMARY KEY (id)
	);

CREATE TABLE einkommensverschlechterung_info (
	id VARCHAR(36) NOT NULL,
	timestamp_erstellt DATETIME NOT NULL,
	timestamp_mutiert DATETIME NOT NULL,
	user_erstellt VARCHAR(36) NOT NULL,
	user_mutiert VARCHAR(36) NOT NULL,
	version BIGINT NOT NULL,
	einkommensverschlechterung BIT NOT NULL,
	ekv_fuer_basis_jahr_plus1 BIT NOT NULL,
	ekv_fuer_basis_jahr_plus2 BIT NOT NULL,
	grund_fuer_basis_jahr_plus1 VARCHAR(255),
	grund_fuer_basis_jahr_plus2 VARCHAR(255),
	stichtag_fuer_basis_jahr_plus1 date,
	stichtag_fuer_basis_jahr_plus2 date,
	PRIMARY KEY (id)
	);

ALTER TABLE einkommensverschlechterung_aud ADD CONSTRAINT FK_einkommensverschlechterung_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_container_aud ADD CONSTRAINT FK_einkommensverschlechterung_container_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_info_aud ADD CONSTRAINT FK_einkommensverschlechterung_info_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT UK_einkommensverschlechterungcontainer_gesuchsteller UNIQUE (gesuchsteller_id);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus1_id FOREIGN KEY (ekvgsbasis_jahr_plus1_id) REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvGSBasisJahrPlus2_id FOREIGN KEY (ekvgsbasis_jahr_plus2_id) REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus1_id FOREIGN KEY (ekvjabasis_jahr_plus1_id) REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT FK_einkommensverschlechterungcontainer_ekvJABasisJahrPlus2_id FOREIGN KEY (ekvjabasis_jahr_plus2_id) REFERENCES einkommensverschlechterung (id);

ALTER TABLE einkommensverschlechterung_container ADD CONSTRAINT FK_einkommensverschlechterungcontainer_gesuchsteller_id FOREIGN KEY (gesuchsteller_id) REFERENCES gesuchsteller (id);

-- ---------------------------------------------------------------------------------------------------------------------
-- Change table gesuch for new einkommensverschlechterung_info
-- ---------------------------------------------------------------------------------------------------------------------
-- remove einkommensverschlechterungs bit on table gesuch
ALTER TABLE gesuch
DROP COLUMN einkommensverschlechterung;
ALTER TABLE gesuch_aud
	DROP COLUMN einkommensverschlechterung;

-- Add einkommensverschlechterung_info on table gesuch
ALTER TABLE gesuch
ADD COLUMN einkommensverschlechterung_info_id VARCHAR(36);
ALTER TABLE gesuch_aud
	ADD COLUMN einkommensverschlechterung_info_id VARCHAR(36);

ALTER TABLE gesuch ADD CONSTRAINT FK_gesuch_einkommensverschlechterung_info_id FOREIGN KEY (einkommensverschlechterung_info_id) REFERENCES einkommensverschlechterung_info (id);