ALTER TABLE kind
  ADD COLUMN einschulung bit DEFAULT 0;

ALTER TABLE kind_aud
  ADD COLUMN einschulung bit;
