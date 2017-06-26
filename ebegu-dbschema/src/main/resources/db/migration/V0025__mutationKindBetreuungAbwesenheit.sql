alter table betreuung add abwesenheit_mutiert bit;
alter table betreuung_aud add abwesenheit_mutiert bit;

alter table betreuung add betreuung_mutiert bit;
alter table betreuung_aud add betreuung_mutiert bit;

alter table kind_container add kind_mutiert bit;
alter table kind_container_aud add kind_mutiert bit;