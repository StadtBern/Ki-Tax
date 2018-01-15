ALTER TABLE fall_aud
	ADD COLUMN verantwortlichersch_id VARCHAR(36);

ALTER TABLE fall
	ADD COLUMN verantwortlichersch_id VARCHAR(36);

alter table fall
	add constraint FK_fall_verantwortlicher_sch_id
foreign key (verantwortlichersch_id)
references benutzer (id);

create index IX_fall_verantwortlicher_sch on fall (verantwortlichersch_id);

#default Verantwortlicher SCH
INSERT INTO application_property (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, name, value)
VALUES ('4aabbd7d-c5e4-4dd4-8b62-344e22ba11fa', '2018-01-15 00:00:00', '2018-01-15 00:00:00', 'flyway', 'flyway', 0,
		'DEFAULT_VERANTWORTLICHER_SCH', 'ebegu');
