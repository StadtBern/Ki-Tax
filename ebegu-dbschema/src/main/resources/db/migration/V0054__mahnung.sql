CREATE TABLE mahnung (
  id                 VARCHAR(36)   NOT NULL,
  timestamp_erstellt DATETIME      NOT NULL,
  timestamp_mutiert  DATETIME      NOT NULL,
  user_erstellt      VARCHAR(36)   NOT NULL,
  user_mutiert       VARCHAR(36)   NOT NULL,
  version            BIGINT        NOT NULL,
  vorgaenger_id      VARCHAR(36),
  active             BIT           NOT NULL,
  bemerkungen        VARCHAR(1000) NOT NULL,
  datum_fristablauf  DATE          NOT NULL,
  mahnung_typ        VARCHAR(255)  NOT NULL,
  gesuch_id          VARCHAR(36)   NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE mahnung_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  vorgaenger_id      VARCHAR(36),
  active             BIT,
  bemerkungen        VARCHAR(1000),
  datum_fristablauf  DATE,
  mahnung_typ        VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

ALTER TABLE mahnung ADD CONSTRAINT FK_mahnung_gesuch_id FOREIGN KEY (gesuch_id) REFERENCES gesuch (id);

ALTER TABLE mahnung_aud ADD CONSTRAINT FK_mahnung_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo (rev);