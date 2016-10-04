alter table gesuch MODIFY eingangsdatum date;

alter table gesuch_aud add COLUMN eingangsdatum date;
alter table gesuch_aud add COLUMN status varchar(255);
alter table gesuch_aud add COLUMN typ varchar(255);
alter table gesuch_aud add COLUMN fall_id varchar(36);
alter table gesuch_aud add COLUMN gesuchsperiode_id varchar(36);