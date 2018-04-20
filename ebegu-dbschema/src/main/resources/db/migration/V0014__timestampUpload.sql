ALTER TABLE dokument ADD timestamp_upload DATETIME NOT NULL DEFAULT '2016-01-01 00:00:00';
ALTER TABLE dokument_aud ADD timestamp_upload DATETIME;

UPDATE dokument SET timestamp_upload = timestamp_mutiert;