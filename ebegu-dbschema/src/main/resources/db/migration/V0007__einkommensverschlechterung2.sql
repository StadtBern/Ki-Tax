ALTER TABLE einkommensverschlechterung ADD COLUMN nettolohn_zus DECIMAL(19, 2);
ALTER TABLE einkommensverschlechterung_aud ADD COLUMN nettolohn_zus DECIMAL(19, 2);

ALTER TABLE einkommensverschlechterung_info ADD COLUMN gemeinsame_steuererklaerung_bjp1 bit;
ALTER TABLE einkommensverschlechterung_info ADD COLUMN gemeinsame_steuererklaerung_bjp2 bit;

ALTER TABLE einkommensverschlechterung_info_aud ADD COLUMN gemeinsame_steuererklaerung_bjp1 bit;
ALTER TABLE einkommensverschlechterung_info_aud ADD COLUMN gemeinsame_steuererklaerung_bjp2 bit;
