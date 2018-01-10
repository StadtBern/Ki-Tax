ALTER TABLE fall_aud
	ADD COLUMN verantwortlichersch_id VARCHAR(36);

ALTER TABLE fall
	ADD COLUMN verantwortlichersch_id VARCHAR(36);

alter table fall
	add constraint FK_fall_verantwortlicher_sch_id
foreign key (verantwortlichersch_id)
references benutzer (id);

create index IX_fall_verantwortlicher_sch on fall (verantwortlichersch_id);
