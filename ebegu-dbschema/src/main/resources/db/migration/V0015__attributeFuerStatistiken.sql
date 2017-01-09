-- AntragHistory neu mit VON-BIS
alter table antrag_status_history change datum timestamp_von DATETIME(6);
alter table antrag_status_history add COLUMN timestamp_bis DATETIME(6);

alter table antrag_status_history_aud change datum timestamp_von DATETIME(6);
alter table antrag_status_history_aud add COLUMN timestamp_bis DATETIME(6);

-- Mahnung mit Timestamp abgeschlossen, anstelle Flag
alter table mahnung add column timestamp_abgeschlossen DATETIME(6);
alter table mahnung_aud add column timestamp_abgeschlossen DATETIME(6);

update mahnung set timestamp_abgeschlossen = now() where active = false;

alter table mahnung drop active;
alter table mahnung_aud drop active;

-- Kategorien auf Verfuegung

alter table verfuegung add column kategorie_kein_pensum bit not null DEFAULT false;
alter table verfuegung add column kategorie_max_einkommen bit not null DEFAULT false;
alter table verfuegung add column kategorie_nicht_eintreten bit not null DEFAULT false;
alter table verfuegung add column kategorie_normal bit not null DEFAULT false;
alter table verfuegung add column kategorie_zuschlag_zum_erwerbspensum bit not null DEFAULT false;

alter table verfuegung_aud add column kategorie_kein_pensum bit not null DEFAULT false;
alter table verfuegung_aud add column kategorie_max_einkommen bit not null DEFAULT false;
alter table verfuegung_aud add column kategorie_nicht_eintreten bit not null DEFAULT false;
alter table verfuegung_aud add column kategorie_normal bit not null DEFAULT false;
alter table verfuegung_aud add column kategorie_zuschlag_zum_erwerbspensum bit not null DEFAULT false;