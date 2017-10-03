ALTER TABLE mitteilung ADD sent_datum DATETIME;
ALTER TABLE mitteilung_aud ADD sent_datum DATETIME;

# notwendig damit man die Mitteilung als Entwurf speichern kann in onBlur
ALTER TABLE mitteilung MODIFY subject VARCHAR(255);
ALTER TABLE mitteilung MODIFY message VARCHAR(4000);
ALTER TABLE mitteilung_aud MODIFY message VARCHAR(4000);
