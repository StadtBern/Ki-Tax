create table adresse (
  id varchar(36) not null,
  timestamp_erstellt timestamp not null,
  timestamp_mutiert timestamp not null,
  user_erstellt varchar(36) not null,
  user_mutiert varchar(36) not null,
  version int8 not null,
  gemeinde varchar(255),
  gueltig_ab timestamp not null,
  gueltig_bis timestamp not null,
  hausnummer varchar(100),
  ort varchar(100) not null,
  plz varchar(4) not null,
  land varchar(255) not null,
  strasse varchar(255) not null,
  zusatzzeile varchar(255),
  constraint PK_adresse primary key (id)
)