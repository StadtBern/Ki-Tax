ALTER TABLE gesuchsteller_adresse ADD COLUMN nicht_in_gemeinde bit not null default false;
ALTER TABLE gesuchsteller_adresse_aud ADD COLUMN nicht_in_gemeinde bit not null default false;

ALTER TABLE verfuegung_zeitabschnitt DROP anspruchspensum_rest;
ALTER TABLE verfuegung_zeitabschnitt DROP erwerbspensumgs1;
ALTER TABLE verfuegung_zeitabschnitt DROP erwerbspensumgs2;
ALTER TABLE verfuegung_zeitabschnitt DROP fachstellenpensum;

ALTER TABLE verfuegung_zeitabschnitt_aud DROP anspruchspensum_rest;
ALTER TABLE verfuegung_zeitabschnitt_aud DROP erwerbspensumgs1;
ALTER TABLE verfuegung_zeitabschnitt_aud DROP erwerbspensumgs2;
ALTER TABLE verfuegung_zeitabschnitt_aud DROP fachstellenpensum;