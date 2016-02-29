    create table application_property (
        id varchar(36) not null,
        timestamp_erstellt timestamp not null,
        timestamp_mutiert timestamp not null,
        user_erstellt varchar(36) not null,
        user_mutiert varchar(36) not null,
        version int8 not null,
        name varchar(255) not null,
        value varchar(1000) not null,
        constraint PK_application_property primary key (id)
    );

    alter table application_property 
        add constraint UK_application_property_name  unique (name);