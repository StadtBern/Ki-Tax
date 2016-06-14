CREATE TABLE fall (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  CONSTRAINT PK_fall PRIMARY KEY (id)
);

CREATE TABLE fall_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  CONSTRAINT PK_fall_aud PRIMARY KEY (id, rev)
);

CREATE TABLE familiensituation (
  id                          VARCHAR(36)  NOT NULL,
  timestamp_erstellt          DATETIME     NOT NULL,
  timestamp_mutiert           DATETIME     NOT NULL,
  user_erstellt               VARCHAR(36)  NOT NULL,
  user_mutiert                VARCHAR(36)  NOT NULL,
  version                     BIGINT       NOT NULL,
  gesuchsteller_kardinalitaet VARCHAR(255),
  bemerkungen                 VARCHAR(1000),
  familienstatus              VARCHAR(255) NOT NULL,
  CONSTRAINT PK_familiensituation PRIMARY KEY (id)
);

CREATE TABLE familiensituation_aud (
  id                          VARCHAR(36) NOT NULL,
  rev                         INTEGER     NOT NULL,
  revtype                     TINYINT,
  timestamp_erstellt          DATETIME,
  timestamp_mutiert           DATETIME,
  user_erstellt               VARCHAR(36),
  user_mutiert                VARCHAR(36),
  gesuchsteller_kardinalitaet VARCHAR(255),
  bemerkungen                 VARCHAR(1000),
  familienstatus              VARCHAR(255),
  CONSTRAINT PK_familiensituation_aud PRIMARY KEY (id, rev)
);

CREATE TABLE gesuch (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt DATETIME    NOT NULL,
  timestamp_mutiert  DATETIME    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  fall_id            VARCHAR(36) NOT NULL,
  familiensituation_id       VARCHAR(36),
  gesuchsteller1_id  VARCHAR(36),
  gesuchsteller2_id  VARCHAR(36),
  CONSTRAINT PK_gesuch PRIMARY KEY (id)
);

CREATE TABLE gesuch_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  fall_id            VARCHAR(36),
  familiensituation_id       VARCHAR(36),
  gesuchsteller1_id  VARCHAR(36),
  gesuchsteller2_id  VARCHAR(36),
  CONSTRAINT PK_gesuch_aud PRIMARY KEY (id, rev)
);

ALTER TABLE fall_aud
ADD CONSTRAINT FK_fall_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_familiensituation_id
FOREIGN KEY (familiensituation_id)
REFERENCES familiensituation (id);

ALTER TABLE familiensituation_aud
ADD CONSTRAINT FK_familiensituation_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE gesuch
ADD CONSTRAINT FK_gesuch_fall_id
FOREIGN KEY (fall_id)
REFERENCES fall (id);

ALTER TABLE gesuch
ADD CONSTRAINT FK_gesuch_gesuchsteller1_id
FOREIGN KEY (gesuchsteller1_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch
ADD CONSTRAINT FK_gesuch_gesuchsteller2_id
FOREIGN KEY (gesuchsteller2_id)
REFERENCES gesuchsteller (id);

ALTER TABLE gesuch_aud
ADD CONSTRAINT FK_gesuch_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);
