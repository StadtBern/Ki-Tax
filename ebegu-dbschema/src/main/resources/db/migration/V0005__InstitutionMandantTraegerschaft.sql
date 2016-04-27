CREATE TABLE institution (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  mandant_id         VARCHAR(36)  NOT NULL,
  traegerschaft_id   VARCHAR(36)  NOT NULL,
  CONSTRAINT PK_institution PRIMARY KEY (id)
);

CREATE TABLE institution_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  mandant_id         VARCHAR(36),
  traegerschaft_id   VARCHAR(36),
  CONSTRAINT PK_institution_aud PRIMARY KEY (id, rev)
);

CREATE TABLE institution_stammdaten (
  id                    VARCHAR(36)    NOT NULL,
  timestamp_erstellt    DATETIME       NOT NULL,
  timestamp_mutiert     DATETIME       NOT NULL,
  user_erstellt         VARCHAR(36)    NOT NULL,
  user_mutiert          VARCHAR(36)    NOT NULL,
  version               BIGINT         NOT NULL,
  gueltig_ab            DATE           NOT NULL,
  gueltig_bis           DATE           NOT NULL,
  betreuungsangebot_typ VARCHAR(255),
  iban                  VARCHAR(255)   NOT NULL,
  oeffnungsstunden      DECIMAL(19, 2) NOT NULL,
  oeffnungstage         DECIMAL(19, 2) NOT NULL,
  institution_id        VARCHAR(36)    NOT NULL,
  CONSTRAINT PK_institution_stammdaten PRIMARY KEY (id)
);

CREATE TABLE institution_stammdaten_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  gueltig_ab            DATE,
  gueltig_bis           DATE,
  betreuungsangebot_typ VARCHAR(255),
  iban                  VARCHAR(255),
  oeffnungsstunden      DECIMAL(19, 2),
  oeffnungstage         DECIMAL(19, 2),
  institution_id        VARCHAR(36),
  CONSTRAINT PK_institution_stammdaten_aud PRIMARY KEY (id, rev)
);

CREATE TABLE mandant (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  CONSTRAINT PK_mandant PRIMARY KEY (id)
);

CREATE TABLE mandant_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  CONSTRAINT mandant PRIMARY KEY (id, rev)
);

CREATE TABLE traegerschaft (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  name               VARCHAR(255) NOT NULL,
  CONSTRAINT PK_traegerschaft PRIMARY KEY (id)
);

CREATE TABLE traegerschaft_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  name               VARCHAR(255),
  CONSTRAINT PK_traegerschaft_aud PRIMARY KEY (id, rev)
);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_mandant_id
FOREIGN KEY (mandant_id)
REFERENCES mandant (id);

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_traegerschaft_if
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE institution_aud
  ADD CONSTRAINT FK_institution_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institution_stammdaten_institution_id
FOREIGN KEY (institution_id)
REFERENCES institution (id);

ALTER TABLE institution_stammdaten_aud
  ADD CONSTRAINT FK_institution_stammdaten_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE mandant_aud
  ADD CONSTRAINT FK_mandant_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE traegerschaft_aud
  ADD CONSTRAINT FK_traegerschaft_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);
