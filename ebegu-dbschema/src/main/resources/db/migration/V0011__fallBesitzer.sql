ALTER TABLE fall ADD besitzer_id varchar(36);
ALTER TABLE fall_aud ADD besitzer_id varchar(36);

alter table fall
  add constraint FK_fall_besitzer_id
foreign key (besitzer_id)
references benutzer (id);

alter table fall
  add constraint UK_fall_besitzer unique (besitzer_id);