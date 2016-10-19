TRUNCATE authorisierter_benutzer;
ALTER TABLE authorisierter_benutzer ADD role VARCHAR(255) NOT NULL;
ALTER TABLE authorisierter_benutzer ADD username VARCHAR(255) NOT NULL;
