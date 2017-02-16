ALTER TABLE verfuegung_zeitabschnitt ADD status VARCHAR(255) not null default 'NEU';
ALTER TABLE verfuegung_zeitabschnitt_aud ADD status VARCHAR(255);