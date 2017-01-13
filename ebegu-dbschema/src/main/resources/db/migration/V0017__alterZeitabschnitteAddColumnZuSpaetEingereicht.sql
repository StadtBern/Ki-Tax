ALTER TABLE verfuegung_zeitabschnitt ADD zu_spaet_eingereicht bit;
UPDATE verfuegung_zeitabschnitt set zu_spaet_eingereicht = true WHERE verfuegung_zeitabschnitt.bemerkungen like '%EINREICHUNGSFRIST%';
ALTER TABLE verfuegung_zeitabschnitt MODIFY zu_spaet_eingereicht bit not null;
ALTER TABLE verfuegung_zeitabschnitt_aud ADD zu_spaet_eingereicht bit;