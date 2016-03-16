create sequence hibernate_sequence start 1 increment 1;
create table application_property_aud (
    id varchar(36) not null,
    rev int4 not null,
    revtype int2,
    name varchar(255),
    value varchar(1000),
    timestamp_erstellt timestamp,
    timestamp_mutiert timestamp,
    user_erstellt varchar(36),
    user_mutiert varchar(36),
    constraint PK_application_property_aud primary  key (id, rev)
  );

create table revinfo (
    rev int4 not null,
    revtstmp int8,
    CONSTRAINT PK_revinfo primary key (rev)
  );

alter table application_property_aud
add constraint FK_application_property_aud_revinfo
foreign key (rev)
references revinfo;


