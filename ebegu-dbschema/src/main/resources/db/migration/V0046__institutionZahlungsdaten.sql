ALTER TABLE institution_stammdaten ADD kontoinhaber varchar(255);
ALTER TABLE institution_stammdaten ADD adresse_kontoinhaber_id varchar(36);

ALTER TABLE institution_stammdaten_aud ADD kontoinhaber varchar(255);
ALTER TABLE institution_stammdaten_aud ADD adresse_kontoinhaber_id varchar(36);

alter table institution_stammdaten add constraint UK_institution_stammdaten_adressekontoinhaber_id unique (adresse_kontoinhaber_id);
alter table institution_stammdaten add constraint FK_institution_stammdaten_adressekontoinhaber_id foreign key (adresse_kontoinhaber_id) references adresse (id);