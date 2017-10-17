create table ferieninsel_zeitraum (
	id varchar(36) not null,
	timestamp_erstellt datetime not null,
	timestamp_mutiert datetime not null,
	user_erstellt varchar(36) not null,
	user_mutiert varchar(36) not null,
	version bigint not null,
	vorgaenger_id varchar(36),
	gueltig_ab date not null,
	gueltig_bis date not null,
	primary key (id)
);

create table ferieninsel_zeitraum_aud (
	id varchar(36) not null,
	rev integer not null,
	revtype tinyint,
	timestamp_erstellt datetime,
	timestamp_mutiert datetime,
	user_erstellt varchar(36),
	user_mutiert varchar(36),
	vorgaenger_id varchar(36),
	gueltig_ab date,
	gueltig_bis date,
	primary key (id, rev)
);

create table ferieninsel_stammdaten (
	id varchar(36) not null,
	timestamp_erstellt datetime not null,
	timestamp_mutiert datetime not null,
	user_erstellt varchar(36) not null,
	user_mutiert varchar(36) not null,
	version bigint not null,
	vorgaenger_id varchar(36),
	anmeldeschluss date not null,
	ferienname varchar(255) not null,
	gesuchsperiode_id varchar(36) not null,
	primary key (id)
);

create table ferieninsel_stammdaten_aud (
	id varchar(36) not null,
	rev integer not null,
	revtype tinyint,
	timestamp_erstellt datetime,
	timestamp_mutiert datetime,
	user_erstellt varchar(36),
	user_mutiert varchar(36),
	vorgaenger_id varchar(36),
	anmeldeschluss date,
	ferienname varchar(255),
	gesuchsperiode_id varchar(36),
	primary key (id, rev)
);

create table ferieninsel_stammdaten_ferieninsel_zeitraum (
	ferieninsel_stammdaten_id varchar(36) not null,
	zeitraum_list_id varchar(36) not null
);

create table ferieninsel_stammdaten_ferieninsel_zeitraum_aud (
	rev integer not null,
	ferieninsel_stammdaten_id varchar(36) not null,
	zeitraum_list_id varchar(36) not null,
	revtype tinyint,
	primary key (rev, ferieninsel_stammdaten_id, zeitraum_list_id)
);

alter table ferieninsel_stammdaten_aud
	add constraint FK_ferieninsel_stammdaten_aud_rev
foreign key (rev)
references revinfo (rev);

alter table ferieninsel_stammdaten_ferieninsel_zeitraum_aud
	add constraint FK_ferieninsel_stammdaten_ferieninsel_zeitraum_aud_rev
foreign key (rev)
references revinfo (rev);

alter table ferieninsel_zeitraum_aud
	add constraint FK_ferieninsel_zeitraum_aud_rev
foreign key (rev)
references revinfo (rev);

alter table ferieninsel_stammdaten
	add constraint FK_ferieninsel_stammdaten_gesuchsperiodeId
foreign key (gesuchsperiode_id)
references gesuchsperiode (id);

alter table ferieninsel_stammdaten_ferieninsel_zeitraum
	add constraint UK_ferieninsel_stammdaten_ferieninsel_zeitraum unique (zeitraum_list_id);

alter table ferieninsel_stammdaten_ferieninsel_zeitraum
	add constraint FK_ferieninsel_stammdaten_ferieninsel_zeitraum_zeitraumListId
foreign key (zeitraum_list_id)
references ferieninsel_zeitraum (id);

alter table ferieninsel_stammdaten_ferieninsel_zeitraum
	add constraint FK_ferieninsel_stammdaten_ferieninsel_zeitraum_stammdatenId
foreign key (ferieninsel_stammdaten_id)
references ferieninsel_stammdaten (id)
