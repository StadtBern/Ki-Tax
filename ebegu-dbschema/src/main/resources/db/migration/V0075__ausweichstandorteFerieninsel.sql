# adds field InsitutionStammdatenFerieninsel to InstitutionStammdaten

create table institution_stammdaten_ferieninsel (
	id                                varchar(36) not null,
	timestamp_erstellt                datetime    not null,
	timestamp_mutiert                 datetime    not null,
	user_erstellt                     varchar(36) not null,
	user_mutiert                      varchar(36) not null,
	version                           bigint      not null,
	vorgaenger_id                     varchar(36),
	ausweichstandort_fruehlingsferien varchar(255),
	ausweichstandort_herbstferien     varchar(255),
	ausweichstandort_sommerferien     varchar(255),
	ausweichstandort_sportferien      varchar(255),
	primary key (id)
);

create table institution_stammdaten_ferieninsel_aud (
	id                                varchar(36) not null,
	rev                               integer     not null,
	revtype                           tinyint,
	timestamp_erstellt                datetime,
	timestamp_mutiert                 datetime,
	user_erstellt                     varchar(36),
	user_mutiert                      varchar(36),
	vorgaenger_id                     varchar(36),
	ausweichstandort_fruehlingsferien varchar(255),
	ausweichstandort_herbstferien     varchar(255),
	ausweichstandort_sommerferien     varchar(255),
	ausweichstandort_sportferien      varchar(255),
	primary key (id, rev)
);

ALTER TABLE institution_stammdaten
	ADD institution_stammdaten_ferieninsel_id varchar(36);
ALTER TABLE institution_stammdaten_aud
	ADD institution_stammdaten_ferieninsel_id varchar(36);

alter table institution_stammdaten_ferieninsel_aud
	add constraint FK_institution_stammdaten_ferieninsel_revinfo
foreign key (rev)
references revinfo (rev);

alter table institution_stammdaten
	add constraint FK_inst_stammdaten_inst_stammdaten_ferieninsel_id
foreign key (institution_stammdaten_ferieninsel_id)
references institution_stammdaten_ferieninsel (id);