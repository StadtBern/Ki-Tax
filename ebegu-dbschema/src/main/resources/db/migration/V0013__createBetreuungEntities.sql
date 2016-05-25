CREATE TABLE betreuung (
  id                        VARCHAR(36)  NOT NULL,
  timestamp_erstellt        DATETIME     NOT NULL,
  timestamp_mutiert         DATETIME     NOT NULL,
  user_erstellt             VARCHAR(36)  NOT NULL,
  user_mutiert              VARCHAR(36)  NOT NULL,
  version                   BIGINT       NOT NULL,
  bemerkungen               VARCHAR(1000),
  betreuungsstatus          VARCHAR(255) NOT NULL,
  schulpflichtig            BIT,
  institution_stammdaten_id VARCHAR(36)  NOT NULL,
  kind_id                   VARCHAR(36)  NOT NULL,
  CONSTRAINT PK_betreuung PRIMARY KEY (id)
);

CREATE TABLE betreuung_aud (
  id                        VARCHAR(36) NOT NULL,
  rev                       INTEGER     NOT NULL,
  revtype                   TINYINT,
  timestamp_erstellt        DATETIME,
  timestamp_mutiert         DATETIME,
  user_erstellt             VARCHAR(36),
  user_mutiert              VARCHAR(36),
  bemerkungen               VARCHAR(1000),
  betreuungsstatus          VARCHAR(255),
  schulpflichtig            BIT,
  institution_stammdaten_id VARCHAR(36),
  kind_id                   VARCHAR(36),
  CONSTRAINT PK_betreuung_aud PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  pensum             INTEGER,
  CONSTRAINT PK_betreuungspensum PRIMARY KEY (id)
);

CREATE TABLE betreuungspensum_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  pensum             INTEGER,
  CONSTRAINT PK_betreuungspensum_aud PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container_aud (
  id                    VARCHAR(36) NOT NULL,
  rev                   INTEGER     NOT NULL,
  revtype               TINYINT,
  timestamp_erstellt    DATETIME,
  timestamp_mutiert     DATETIME,
  user_erstellt         VARCHAR(36),
  user_mutiert          VARCHAR(36),
  betreuung_id          VARCHAR(36),
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  CONSTRAINT PK_betreuungspensum_container_aud PRIMARY KEY (id, rev)
);

CREATE TABLE betreuungspensum_container (
  id                    VARCHAR(36) NOT NULL,
  timestamp_erstellt    DATETIME    NOT NULL,
  timestamp_mutiert     DATETIME    NOT NULL,
  user_erstellt         VARCHAR(36) NOT NULL,
  user_mutiert          VARCHAR(36) NOT NULL,
  version               BIGINT      NOT NULL,
  betreuung_id          VARCHAR(36) NOT NULL,
  betreuungspensumgs_id VARCHAR(36),
  betreuungspensumja_id VARCHAR(36),
  CONSTRAINT PK_betreuungspensum_container PRIMARY KEY (id)
);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_institution_stammdaten_id
FOREIGN KEY (institution_stammdaten_id)
REFERENCES institution_stammdaten (id);

ALTER TABLE betreuung
  ADD CONSTRAINT FK_betreuung_kind_id
FOREIGN KEY (kind_id)
REFERENCES kind_container (id);

ALTER TABLE betreuung_aud
  ADD CONSTRAINT FK_betreuung_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_aud
  ADD CONSTRAINT FK_betreuungspensum_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_container_aud
  ADD CONSTRAINT FK_betreuungspensum_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuung_id
FOREIGN KEY (betreuung_id)
REFERENCES betreuung (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_gs
FOREIGN KEY (betreuungspensumgs_id)
REFERENCES betreuungspensum (id);

ALTER TABLE betreuungspensum_container
  ADD CONSTRAINT FK_betreuungspensum_container_betreuungspensum_ja
FOREIGN KEY (betreuungspensumja_id)
REFERENCES betreuungspensum (id);