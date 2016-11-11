ALTER TABLE betreuungspensum
  ADD COLUMN nicht_eingetreten bit NOT NULL DEFAULT FALSE;

ALTER TABLE betreuungspensum_aud
  ADD COLUMN nicht_eingetreten bit;