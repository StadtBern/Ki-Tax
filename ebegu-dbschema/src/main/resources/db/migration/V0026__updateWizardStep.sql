delimiter //
create trigger createUUIDONInsert  BEFORE INSERT on wizard_step
FOR EACH ROW
BEGIN
  SET new.id := (SELECT UUID());
END;//
delimiter ;


INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'GESUCH_ERSTELLEN',
    'OK',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'GESUCH_ERSTELLEN');


INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'FAMILIENSITUATION',
    'IN_BEARBEITUNG',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'FAMILIENSITUATION');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'GESUCHSTELLER',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'GESUCHSTELLER');
INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'KINDER',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'KINDER');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'BETREUUNG',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'BETREUUNG');
INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'ERWERBSPENSUM',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'ERWERBSPENSUM');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'FINANZIELLE_SITUATION',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'FINANZIELLE_SITUATION');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'EINKOMMENSVERSCHLECHTERUNG',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'EINKOMMENSVERSCHLECHTERUNG');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'DOKUMENTE',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'DOKUMENTE');

INSERT INTO wizard_step (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, bemerkungen, wizard_step_name, wizard_step_status, gesuch_id)
  SELECT DISTINCT
    'dummyid',
    now(),
    now(),
    'flyway',
    'flyway',
    0,
    NULL,
    'VERFUEGEN',
    'UNBESUCHT',
    gesuch.id
  FROM gesuch where gesuch.id not in (select wizard_step.gesuch_id from wizard_step where wizard_step_name = 'VERFUEGEN');

DROP TRIGGER createUUIDONInsert;



