ALTER TABLE verfuegung_zeitabschnitt ADD zahlungsstatus VARCHAR(255) NOT NULL DEFAULT 'NEU';
ALTER TABLE verfuegung_zeitabschnitt_aud ADD zahlungsstatus VARCHAR(255);

ALTER TABLE zahlungsauftrag DROP ausgeloest;
ALTER TABLE zahlungsauftrag_aud DROP ausgeloest;

ALTER TABLE zahlungsauftrag ADD status VARCHAR(255) NOT NULL DEFAULT 'ENTWURF';
ALTER TABLE zahlungsauftrag_aud ADD status VARCHAR(255);

ALTER TABLE zahlungsauftrag MODIFY datum_faellig DATE NOT NULL;
ALTER TABLE zahlungsauftrag_aud MODIFY datum_faellig DATE;

ALTER TABLE zahlungsposition ADD ignoriert BIT NOT NULL;
ALTER TABLE zahlungsposition_aud ADD ignoriert BIT;