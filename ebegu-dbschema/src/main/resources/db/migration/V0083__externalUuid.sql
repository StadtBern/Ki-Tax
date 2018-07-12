ALTER TABLE benutzer ADD externaluuid VARCHAR(36);
ALTER TABLE benutzer_aud ADD externaluuid VARCHAR(36);

CREATE INDEX IX_benutzer_externalUUID
	ON benutzer (externaluuid);

ALTER TABLE benutzer
	ADD CONSTRAINT UK_externalUUID UNIQUE (externaluuid);