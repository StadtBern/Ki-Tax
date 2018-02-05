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
    requesturi varchar(255) not null,
    result_data varchar(255) null,
    startinguser varchar(255) not null,
    status varchar(255) not null,
    triggering_ip varchar(45),
    work_job_type varchar(255) not null,
    primary key (id)
);