CREATE TABLE gesuchsperiode (
  id                 VARCHAR(36) NOT NULL,
  timestamp_erstellt datetime    NOT NULL,
  timestamp_mutiert  datetime    NOT NULL,
  user_erstellt      VARCHAR(36) NOT NULL,
  user_mutiert       VARCHAR(36) NOT NULL,
  version            BIGINT      NOT NULL,
  gueltig_ab         DATE        NOT NULL,
  gueltig_bis        DATE        NOT NULL,
  active             BIT         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gesuchsperiode_aud (
  id                 VARCHAR(36) NOT NULL,
  rev                INTEGER     NOT NULL,
  revtype            tinyint,
  timestamp_erstellt datetime,
  timestamp_mutiert  datetime,
  user_erstellt      VARCHAR(36),
  user_mutiert       VARCHAR(36),
  gueltig_ab         DATE,
  gueltig_bis        DATE,
  active             BIT,
  PRIMARY KEY (id, rev)
);

ALTER TABLE gesuchsperiode_aud
  ADD CONSTRAINT FK_gesuchsperiode_aud_revinfo
FOREIGN KEY (rev)
REFERENCES revinfo (rev);

INSERT INTO ebegu.gesuchsperiode (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gueltig_ab, gueltig_bis, active) VALUES ('0621fb5d-a187-5a91-abaf-8a813c4d263a', '2016-05-30 16:39:38', '2016-05-30 16:39:38', 'anonymous', 'anonymous', 0, '2016-08-01', '2017-07-31', true);

ALTER TABLE gesuch
  ADD gesuchsperiode_id VARCHAR(36) NOT NULL DEFAULT '0621fb5d-a187-5a91-abaf-8a813c4d263a';

ALTER TABLE gesuch_aud
  ADD gesuchsperiode_id VARCHAR(36);

ALTER TABLE gesuch
  ADD CONSTRAINT FK_gesuch_gesuchsperiode_id
FOREIGN KEY (gesuchsperiode_id)
REFERENCES gesuchsperiode (id);
