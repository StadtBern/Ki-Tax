ALTER TABLE traegerschaft
  ADD COLUMN active BIT;

UPDATE traegerschaft
SET active = '1'
WHERE id = '71cba831-f044-44e2-a8a2-21376da8a959';

ALTER TABLE traegerschaft
  MODIFY active BIT NOT NULL;

ALTER TABLE traegerschaft_aud
  ADD COLUMN active BIT;