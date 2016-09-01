CREATE TABLE wizard_step_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            TINYINT,
  timestamp_erstellt DATETIME,
  timestamp_mutiert  DATETIME,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  bemerkungen        VARCHAR(1000),
  wizard_step_name   VARCHAR(255),
  wizard_step_status VARCHAR(255),
  gesuch_id          VARCHAR(36),
  PRIMARY KEY (id, rev)
);

CREATE TABLE wizard_step (
  id                 VARCHAR(36)  NOT NULL,
  timestamp_erstellt DATETIME     NOT NULL,
  timestamp_mutiert  DATETIME     NOT NULL,
  user_erstellt      VARCHAR(36)  NOT NULL,
  user_mutiert       VARCHAR(36)  NOT NULL,
  version            BIGINT       NOT NULL,
  bemerkungen        VARCHAR(1000),
  wizard_step_name   VARCHAR(255) NOT NULL,
  wizard_step_status VARCHAR(255) NOT NULL,
  gesuch_id          VARCHAR(36)  NOT NULL,
  PRIMARY KEY (id)
);

ALTER TABLE wizard_step_aud
  ADD CONSTRAINT FK_wizard_step_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

ALTER TABLE wizard_step
  ADD CONSTRAINT UK_wizardstep_gesuch_stepname UNIQUE (wizard_step_name, gesuch_id);

ALTER TABLE wizard_step
  ADD CONSTRAINT FK_wizardstep_gesuch_id
FOREIGN KEY (gesuch_id)
REFERENCES gesuch (id);