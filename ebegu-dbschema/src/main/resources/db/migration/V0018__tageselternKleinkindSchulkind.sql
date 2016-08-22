alter table betreuung drop schulpflichtig;
alter table betreuung_aud drop schulpflichtig;

update institution_stammdaten set betreuungsangebot_typ = 'TAGESELTERN_KLEINKIND' where betreuungsangebot_typ = 'TAGESELTERN';
update institution_stammdaten_aud set betreuungsangebot_typ = 'TAGESELTERN_KLEINKIND' where betreuungsangebot_typ = 'TAGESELTERN';