ALTER TABLE mitteilung ADD betreuung_id VARCHAR(36);
ALTER TABLE mitteilung_aud ADD betreuung_id VARCHAR(36);

ALTER TABLE mitteilung
  ADD CONSTRAINT FK_mitteilung_betreuung_id FOREIGN KEY (betreuung_id) REFERENCES betreuung (id);
