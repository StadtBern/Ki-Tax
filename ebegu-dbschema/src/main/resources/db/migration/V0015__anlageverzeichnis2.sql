ALTER TABLE dokument_grund CHANGE fullName full_name VARCHAR(255);
ALTER TABLE dokument_grund_aud CHANGE fullName full_name VARCHAR(255);

ALTER TABLE dokument ADD COLUMN dokument_pfad VARCHAR(255);
ALTER TABLE dokument_aud ADD COLUMN dokument_pfad VARCHAR(255);

ALTER TABLE dokument ADD COLUMN dokument_size VARCHAR(255);
ALTER TABLE dokument_aud ADD COLUMN dokument_size VARCHAR(255);