ALTER TABLE gesuch ADD gesperrt_wegen_beschwerde BIT NOT NULL DEFAULT FALSE;
ALTER TABLE gesuch_aud ADD gesperrt_wegen_beschwerde BIT;