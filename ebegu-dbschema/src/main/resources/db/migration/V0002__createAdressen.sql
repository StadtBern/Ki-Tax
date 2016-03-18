create table adresse (
  id varchar(36) not null,
  timestamp_erstellt timestamp not null,
  timestamp_mutiert timestamp not null,
  user_erstellt varchar(36) not null,
  user_mutiert varchar(36) not null,
  version int8 not null,
  gemeinde varchar(255),
  hausnummer varchar(100),
  ort varchar(100) not null,
  plz varchar(4),
  postfach varchar(100),
  strasse varchar(255) not null,
  constraint PK_adresse primary key (id)
)