CREATE TABLE finanzielle_situation_aud (
  id                                VARCHAR(36) NOT NULL,
  rev                               INTEGER     NOT NULL,
  revtype                           TINYINT,
  timestamp_erstellt                DATETIME,
  timestamp_mutiert                 DATETIME,
  user_erstellt                     VARCHAR(36),
  user_mutiert                      VARCHAR(36),
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  selbstaendig                      BIT,
  steuererklaerung_ausgefuellt      BIT,
  steuerveranlagung_erhalten        BIT,
  CONSTRAINT PK_finanzielle_situation_aud PRIMARY KEY (id)
);

CREATE TABLE finanzielle_situation_container_aud (
  id                         VARCHAR(36) NOT NULL,
  rev                        INTEGER     NOT NULL,
  revtype                    TINYINT,
  timestamp_erstellt         DATETIME,
  timestamp_mutiert          DATETIME,
  user_erstellt              VARCHAR(36),
  user_mutiert               VARCHAR(36),
  jahr                       INTEGER,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  finanzielle_situationsv_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36),
  CONSTRAINT PK_finanzielle_situation_container_aud PRIMARY KEY (id)
);

CREATE TABLE finanzielle_situation (
  id                                VARCHAR(36) NOT NULL,
  timestamp_erstellt                DATETIME    NOT NULL,
  timestamp_mutiert                 DATETIME    NOT NULL,
  user_erstellt                     VARCHAR(36) NOT NULL,
  user_mutiert                      VARCHAR(36) NOT NULL,
  version                           BIGINT      NOT NULL,
  bruttovermoegen                   DECIMAL(19, 2),
  erhaltene_alimente                DECIMAL(19, 2),
  ersatzeinkommen                   DECIMAL(19, 2),
  familienzulage                    DECIMAL(19, 2),
  geleistete_alimente               DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr        DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus1 DECIMAL(19, 2),
  geschaeftsgewinn_basisjahr_minus2 DECIMAL(19, 2),
  nettolohn                         DECIMAL(19, 2),
  schulden                          DECIMAL(19, 2),
  selbstaendig                      BIT         NOT NULL,
  steuererklaerung_ausgefuellt      BIT         NOT NULL,
  steuerveranlagung_erhalten        BIT         NOT NULL,
  CONSTRAINT PK_finanzielle_situation PRIMARY KEY (id)
);

CREATE TABLE finanzielle_situation_container (
  id                         VARCHAR(36) NOT NULL,
  timestamp_erstellt         DATETIME    NOT NULL,
  timestamp_mutiert          DATETIME    NOT NULL,
  user_erstellt              VARCHAR(36) NOT NULL,
  user_mutiert               VARCHAR(36) NOT NULL,
  version                    BIGINT      NOT NULL,
  jahr                       INTEGER     NOT NULL,
  finanzielle_situationgs_id VARCHAR(36),
  finanzielle_situationja_id VARCHAR(36),
  finanzielle_situationsv_id VARCHAR(36),
  gesuchsteller_id           VARCHAR(36) NOT NULL,
  CONSTRAINT PK_finanzielle_situation_container PRIMARY KEY (id)
);

ALTER TABLE finanzielle_situation_aud
  ADD CONSTRAINT fk_finanzielle_situation_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE finanzielle_situation_container_aud
  ADD CONSTRAINT fk_finanzielle_situation_container_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT fk_finanzielle_situation_container_gs
FOREIGN KEY (finanzielle_situationgs_id)
REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT fk_finanzielle_situation_container_ja
FOREIGN KEY (finanzielle_situationja_id)
REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT fk_finanzielle_situation_container_sv
FOREIGN KEY (finanzielle_situationsv_id)
REFERENCES finanzielle_situation (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT fk_finanzielle_situation_container_gesuchsteller
FOREIGN KEY (gesuchsteller_id)
REFERENCES gesuchsteller (id);

ALTER TABLE finanzielle_situation_container
  ADD CONSTRAINT UK_finanzielle_situation_container_gesuchsteller
UNIQUE (gesuchsteller_id);
