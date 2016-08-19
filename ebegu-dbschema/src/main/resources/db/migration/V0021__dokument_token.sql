create table temp_dokument (
    id varchar(36) not null,
    timestamp_erstellt datetime not null,
    timestamp_mutiert datetime not null,
    user_erstellt varchar(36) not null,
    user_mutiert varchar(36) not null,
    version bigint not null,
    access_token varchar(36) not null,
    dokument_id varchar(36) not null,
    ip varchar(45) not null,
    primary key (id)
);

alter table temp_dokument
    add constraint FK_tempdokument_dokument_id
    foreign key (dokument_id)
    references dokument (id)
    on delete cascade;