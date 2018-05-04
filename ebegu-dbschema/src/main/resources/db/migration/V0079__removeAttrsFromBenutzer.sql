ALTER TABLE benutzer DROP FOREIGN KEY FK_benutzer_institution_id;
ALTER TABLE benutzer DROP FOREIGN KEY FK_benutzer_traegerschaft_id;

ALTER TABLE benutzer DROP COLUMN role;
ALTER TABLE benutzer DROP COLUMN institution_id;
ALTER TABLE benutzer DROP COLUMN traegerschaft_id;

-- Bei AuditTabellen absichtlich drinn gelassen, da uns sonst diese History verloren geht!