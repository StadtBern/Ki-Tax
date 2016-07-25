CREATE TABLE dokument 
  ( 
     id                 VARCHAR(36) NOT NULL, 
     timestamp_erstellt DATETIME NOT NULL, 
     timestamp_mutiert  DATETIME NOT NULL, 
     user_erstellt      VARCHAR(36) NOT NULL, 
     user_mutiert       VARCHAR(36) NOT NULL, 
     version            BIGINT NOT NULL, 
     dokument_name      VARCHAR(255) NOT NULL, 
     dokument_typ       VARCHAR(255), 
     dokument_grund_id  VARCHAR(36) NOT NULL, 
     PRIMARY KEY (id) 
  ); 

CREATE TABLE dokument_aud 
  ( 
     id                 VARCHAR(36) NOT NULL, 
     rev                INTEGER NOT NULL, 
     revtype            TINYINT, 
     timestamp_erstellt DATETIME, 
     timestamp_mutiert  DATETIME, 
     user_erstellt      VARCHAR(36), 
     user_mutiert       VARCHAR(36), 
     dokument_name      VARCHAR(255), 
     dokument_typ       VARCHAR(255), 
     dokument_grund_id  VARCHAR(36), 
     PRIMARY KEY (id, rev) 
  ); 

CREATE TABLE dokument_grund_aud 
  ( 
     id                 VARCHAR(36) NOT NULL, 
     rev                INTEGER NOT NULL, 
     revtype            TINYINT, 
     timestamp_erstellt DATETIME, 
     timestamp_mutiert  DATETIME, 
     user_erstellt      VARCHAR(36), 
     user_mutiert       VARCHAR(36), 
     dokument_grund_typ VARCHAR(255), 
     fullname           VARCHAR(255),
     tag                VARCHAR(255),
     gesuch_id          VARCHAR(36), 
     PRIMARY KEY (id, rev) 
  ); 

CREATE TABLE dokument_grund 
  ( 
     id                 VARCHAR(36) NOT NULL, 
     timestamp_erstellt DATETIME NOT NULL, 
     timestamp_mutiert  DATETIME NOT NULL, 
     user_erstellt      VARCHAR(36) NOT NULL, 
     user_mutiert       VARCHAR(36) NOT NULL, 
     version            BIGINT NOT NULL, 
     dokument_grund_typ VARCHAR(255), 
     fullname           VARCHAR(255),
     tag                VARCHAR(255),
     gesuch_id          VARCHAR(36) NOT NULL, 
     PRIMARY KEY (id) 
  ); 

ALTER TABLE dokument 
  ADD CONSTRAINT fk_dokument_dokumentgrund_id FOREIGN KEY (dokument_grund_id) 
  REFERENCES dokument_grund (id); 

ALTER TABLE dokument_aud 
  ADD CONSTRAINT fk_dokument_aud_dokumentgrund_id FOREIGN KEY (rev) REFERENCES 
  revinfo (rev); 

ALTER TABLE dokument_grund_aud 
  ADD CONSTRAINT fk_dokumentgrund_aud_gesuch_id FOREIGN KEY (rev) REFERENCES 
  revinfo (rev); 

ALTER TABLE dokument_grund 
  ADD CONSTRAINT fk_dokumentgrund_gesuch_id FOREIGN KEY (gesuch_id) REFERENCES 
  gesuch (id); 