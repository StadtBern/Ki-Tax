CREATE TABLE ebegu_parameter (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  gueltig_ab         DATE         NOT NULL,
  gueltig_bis        DATE         NOT NULL,
  name               VARCHAR(255) NOT NULL,
  value              VARCHAR(255) NOT NULL,
  CONSTRAINT PK_ebeguparameter PRIMARY KEY (id)
);

CREATE TABLE ebegu_parameter_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  name               VARCHAR(255),
  value              VARCHAR(255),
  CONSTRAINT PK_ebeguparameter_aud PRIMARY KEY (id, rev)
);

ALTER TABLE ebegu_parameter_aud
  ADD CONSTRAINT FK_ebeguparameter_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev)