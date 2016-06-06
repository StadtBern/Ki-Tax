alter table gesuchsteller add diplomatenstatus bit not null;
alter table gesuchsteller_aud add diplomatenstatus bit not null;

alter table kind add kinderabzug varchar(255) not null;
alter table kind drop COLUMN unterstuetzungspflicht;
alter table kind drop COLUMN wohnhaft_im_gleichen_haushalt;

alter table kind_aud add kinderabzug varchar(255) not null;
alter table kind_aud drop COLUMN unterstuetzungspflicht;
alter table kind_aud drop COLUMN wohnhaft_im_gleichen_haushalt;