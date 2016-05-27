CREATE TABLE erwerbspensum (
  id                               VARCHAR(36)  NOT NULL,
  timestamp_erstellt               DATETIME     NOT NULL,
  timestamp_mutiert                DATETIME     NOT NULL,
  user_erstellt                    VARCHAR(36)  NOT NULL,
  user_mutiert                     VARCHAR(36)  NOT NULL,
  version                          BIGINT       NOT NULL,
  gueltig_ab                       DATE         NOT NULL,
  gueltig_bis                      DATE         NOT NULL,
  pensum                           INTEGER,
  gesundheitliche_einschraenkungen BIT          NOT NULL,
  taetigkeit                       VARCHAR(255) NOT NULL,
  zuschlag_zu_erwerbspensum        BIT          NOT NULL,
  zuschlagsgrund                   VARCHAR(255),
  zuschlagsprozent                 INTEGER,
  CONSTRAINT PK_erwerbspensum PRIMARY KEY (id)
);

CREATE TABLE erwerbspensum_aud (
  id                               VARCHAR(36) NOT NULL,
  rev                              INTEGER     NOT NULL,
  revtype                          TINYINT,
  timestamp_erstellt               DATETIME,
  timestamp_mutiert                DATETIME,
  user_erstellt                    VARCHAR(36),
  user_mutiert                     VARCHAR(36),
  gueltig_ab                       DATE,
  gueltig_bis                      DATE,
  pensum                           INTEGER,
  gesundheitliche_einschraenkungen BIT,
  taetigkeit                       VARCHAR(255),
  zuschlag_zu_erwerbspensum        BIT,
  zuschlagsgrund                   VARCHAR(255),
  zuschlagsprozent                 INTEGER,
  CONSTRAINT PK_erwerbspensum_aud PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36),
  CONSTRAINT PK_erwerbspensum_container_aud PRIMARY KEY (id, rev)
);

CREATE TABLE erwerbspensum_container (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  erwerbspensumgs_id VARCHAR(36),
  erwerbspensumja_id VARCHAR(36),
  gesuchsteller_id   VARCHAR(36) NOT NULL,
  CONSTRAINT PK_erwerbspensum_container PRIMARY KEY (id)
);

ALTER TABLE erwerbspensum_aud
  ADD CONSTRAINT FK_erwerbspensum_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE erwerbspensum_container_aud
  ADD CONSTRAINT FK_erwerbspensum_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_ErwerbspensumContainer_erwerbspensumgs_id
FOREIGN KEY (erwerbspensumgs_id)
REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_ErwerbspensumContainer_erwerbspensumja_id
FOREIGN KEY (erwerbspensumja_id)
REFERENCES erwerbspensum (id);

ALTER TABLE erwerbspensum_container
  ADD CONSTRAINT FK_ErwerbspensumContainer_gesuchsteller_id
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);
