ALTER TABLE verfuegung_zeitabschnitt ADD zahlungsstatus VARCHAR(255) not null default 'NEU';
ALTER TABLE verfuegung_zeitabschnitt_aud ADD zahlungsstatus VARCHAR(255);