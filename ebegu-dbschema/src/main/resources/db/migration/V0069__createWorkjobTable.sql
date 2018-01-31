create table workjob (
    id varchar(36) not null,
    timestamp_erstellt datetime not null,
    timestamp_mutiert datetime not null,
    user_erstellt varchar(36) not null,
    user_mutiert varchar(36) not null,
    version bigint not null,
    vorgaenger_id varchar(36),
    execution_id bigint,
    metadata longtext,
    params varchar(255) not null,
    startinguser varchar(255) not null,
    status varchar(255) not null,
    triggering_ip varchar(45),
    work_job_type varchar(255) not null,
    primary key (id)
);

create table workpackage (
    id varchar(36) not null,
    timestamp_erstellt datetime not null,
    timestamp_mutiert datetime not null,
    user_erstellt varchar(36) not null,
    user_mutiert varchar(36) not null,
    version bigint not null,
    vorgaenger_id varchar(36),
    work_package_seq_number integer not null,
    work_row_result longtext,
    workjob_id varchar(36) not null,
    primary key (id)
);


alter table workpackage
    add constraint UK_workpkg_workjob_seq_num unique (workjob_id, work_package_seq_number);

alter table workpackage
    add constraint FK_workpkg_workjob_id
    foreign key (workjob_id)
    references workjob (id);