ALTER TABLE dokument_grund CHANGE fullname full_name VARCHAR(255);
ALTER TABLE dokument_grund_aud CHANGE fullname full_name VARCHAR(255);

ALTER TABLE dokument ADD COLUMN dokument_pfad VARCHAR(255);
ALTER TABLE dokument_aud ADD COLUMN dokument_pfad VARCHAR(255);

ALTER TABLE dokument ADD COLUMN dokument_size VARCHAR(255);
ALTER TABLE dokument_aud ADD COLUMN dokument_size VARCHAR(255);

ALTER TABLE dokument_grund ADD COLUMN dokument_typ VARCHAR(255);
ALTER TABLE dokument_grund_aud ADD COLUMN dokument_typ VARCHAR(255);

ALTER TABLE dokument DROP COLUMN dokument_typ;
ALTER TABLE dokument_aud DROP COLUMN dokument_typ;