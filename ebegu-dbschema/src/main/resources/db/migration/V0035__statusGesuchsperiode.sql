ALTER TABLE gesuchsperiode ADD status VARCHAR(255) NOT NULL DEFAULT 'ENTWURF';
ALTER TABLE gesuchsperiode_aud ADD status VARCHAR(255) NOT NULL DEFAULT 'ENTWURF';

UPDATE gesuchsperiode SET status = 'AKTIV' WHERE active = TRUE;
UPDATE gesuchsperiode_aud SET status = 'AKTIV' WHERE active = TRUE;

ALTER TABLE gesuchsperiode DROP COLUMN active;
ALTER TABLE gesuchsperiode_aud DROP COLUMN active;

