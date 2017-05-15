update application_property set name = 'ANZAHL_TAGE_BIS_WARNUNG_FREIGABE' where name = 'ANZAHL_MONATE_BIS_WARNUNG_FREIGABE';
update application_property set name = 'ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG' where name = 'ANZAHL_MONATE_BIS_WARNUNG_QUITTUNG';
update application_property set name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE' where name = 'ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE';
update application_property set name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG' where name = 'ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG';

update application_property_aud set name = 'ANZAHL_TAGE_BIS_WARNUNG_FREIGABE' where name = 'ANZAHL_MONATE_BIS_WARNUNG_FREIGABE';
update application_property_aud set name = 'ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG' where name = 'ANZAHL_MONATE_BIS_WARNUNG_QUITTUNG';
update application_property_aud set name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE' where name = 'ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE';
update application_property_aud set name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG' where name = 'ANZAHL_MONATE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG';

update application_property set value = '60' where name = 'ANZAHL_TAGE_BIS_WARNUNG_FREIGABE';
update application_property set value = '15' where name = 'ANZAHL_TAGE_BIS_WARNUNG_QUITTUNG';
update application_property set value = '60' where name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_FREIGABE';
update application_property set value = '90' where name = 'ANZAHL_TAGE_BIS_LOESCHUNG_NACH_WARNUNG_QUITTUNG';

alter table gesuch drop COLUMN gewarnt_nicht_freigegeben;
alter table gesuch drop COLUMN gewarnt_fehlende_quittung;

alter table gesuch_aud drop COLUMN gewarnt_nicht_freigegeben;
alter table gesuch_aud drop COLUMN gewarnt_fehlende_quittung;

ALTER TABLE gesuch ADD datum_gewarnt_nicht_freigegeben DATE;
ALTER TABLE gesuch ADD datum_gewarnt_fehlende_quittung DATE;

ALTER TABLE gesuch_aud ADD datum_gewarnt_nicht_freigegeben DATE;
ALTER TABLE gesuch_aud ADD datum_gewarnt_fehlende_quittung DATE;