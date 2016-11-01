TRUNCATE authorisierter_benutzer;
ALTER TABLE authorisierter_benutzer ADD role VARCHAR(255) NOT NULL;
ALTER TABLE authorisierter_benutzer ADD username VARCHAR(255) NOT NULL;


ALTER TABLE authorisierter_benutzer ADD saml_name_id varchar(255);
ALTER TABLE authorisierter_benutzer ADD session_index varchar(255);
ALTER TABLE authorisierter_benutzer ADD samlspentityid varchar(255);
ALTER TABLE authorisierter_benutzer ADD samlidpentityid varchar(255);
