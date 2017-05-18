# Deletion of a complete KindContainer for Fall 253

DELETE FROM betreuungspensum_container
WHERE id = 'e30fcf4e-66a5-4427-9fe8-ce1cea22bcd9';
DELETE FROM betreuungspensum
WHERE id = 'e1567b5f-50de-4b21-888b-530a41df0865';
DELETE FROM betreuungspensum
WHERE id = '2c1d85bb-5f11-4b33-b417-88cb866a3846';
DELETE FROM betreuung
WHERE id = 'eb059092-d64b-45f3-8c66-ef9238a6206d';

DELETE FROM kind_container
WHERE id = '024cf329-9b95-4b63-8a3c-bdd7e45e5370';
DELETE FROM kind
WHERE id = '5b633045-e130-4633-b1ff-4bf19d869715';
DELETE FROM kind
WHERE id = 'ff831551-ff10-4bb1-b55b-4f30c068365c';

UPDATE wizard_step
SET timestamp_mutiert = now(), user_mutiert = 'gugler17', version = 10,
  wizard_step_status  = 'OK'
WHERE id = 'f7ac9098-a27b-42a8-b2fa-930c3efe1a41';
