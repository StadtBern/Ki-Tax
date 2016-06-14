# ----------------------------------------------------------------------------------------------------------------------
# Traegerschaft ist nicht mehr Pflicht auf der Institution und wird somit entfernt
# ----------------------------------------------------------------------------------------------------------------------
ALTER TABLE institution DROP FOREIGN KEY FK_institution_traegerschaft_if;

ALTER TABLE institution
  ADD CONSTRAINT FK_institution_traegerschaft_id
FOREIGN KEY (traegerschaft_id)
REFERENCES traegerschaft (id);

ALTER TABLE institution MODIFY COLUMN traegerschaft_id VARCHAR(36);

# ----------------------------------------------------------------------------------------------------------------------
# Active flag muss auf der Institution hinzugefügt werden, um inaktive Institutionen zu markieren
# ----------------------------------------------------------------------------------------------------------------------

# Active flag zuerst als not required hinzufügen
ALTER TABLE institution add active BIT;

# bei vorhandene Institutionen active auf true setzen
UPDATE institution set active = 1 WHERE institution.id = '1339bf45-d69e-4e30-ba40-036238c4e47b';
UPDATE institution set active = 1 WHERE institution.id = '9253e9b1-9cae-4278-b578-f1ce93306d29';
UPDATE institution set active = 1 WHERE institution.id = '7ec09189-de4b-4c87-afcc-1f24f55e6dcd';

# Anschliessend active als required setzen
ALTER TABLE institution MODIFY active BIT NOT NULL;

ALTER TABLE institution_aud add active BIT NOT NULL;

# ----------------------------------------------------------------------------------------------------------------------
# Eine Adresse soll in der Institutions Stammdaten hinzugefügt werden
# ----------------------------------------------------------------------------------------------------------------------

# In Tabelle Stammdaten wird die Adresse hinzugefügt
ALTER TABLE institution_stammdaten add adresse_id VARCHAR(36);
ALTER TABLE institution_stammdaten_aud add adresse_id VARCHAR(36);

# Da die adresse Pflicht ist werden dummy Adressen erstellt...
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('efce0c7a-5c7e-4037-818b-c7ee00558884', '2016-05-31 09:17:36', '2016-05-31 09:17:36', 'anonymous', 'anonymous', 0, null, '1000-01-01', '9999-12-31', '1', 'PW', 'Bern', '3001', 'Länggasse', 'Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('9c0157ba-2c2d-49f0-ab61-2363ce18204a', '2016-05-31 09:17:36', '2016-05-31 09:17:36', 'anonymous', 'anonymous', 0, null, '1000-01-01', '9999-12-31', '2', 'PW', 'Bern', '3002', 'Breitenrain', 'Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('8d08e0ea-56b6-4764-93c2-e4d945bbc021', '2016-05-31 09:17:36', '2016-05-31 09:17:36', 'anonymous', 'anonymous', 0, null, '1000-01-01', '9999-12-31', '3', 'PW', 'Bern', '3003', 'Bahnhofstrasse', 'Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('f63e05b9-e066-4aec-8222-4e15f382fc70', '2016-05-31 09:17:36', '2016-05-31 09:17:36', 'anonymous', 'anonymous', 0, null, '1000-01-01', '9999-12-31', '4', 'PW', 'Bern', '3004', 'Könizweg', 'Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');
INSERT INTO adresse (id, timestamp_erstellt, timestamp_mutiert, user_erstellt, user_mutiert, version, gemeinde, gueltig_ab, gueltig_bis, hausnummer, land, ort, plz, strasse, zusatzzeile)
VALUES ('df293958-1125-4f78-bec3-97df287de9cd', '2016-05-31 09:17:36', '2016-05-31 09:17:36', 'anonymous', 'anonymous', 0, null, '1000-01-01', '9999-12-31', '5', 'PW', 'Bern', '3005', 'Worbstrasse', 'Quidem debitis molestiae ullamco hic sequi odio voluptatibus maiores voluptate vero libero aliquam quisquam dolore');

# ... und allen Stammdaten hinzugefügt
UPDATE institution_stammdaten set adresse_id = 'efce0c7a-5c7e-4037-818b-c7ee00558884' WHERE institution_stammdaten.id = '0621fb5d-a187-4a91-abaf-87812c4d261a';
UPDATE institution_stammdaten set adresse_id = '9c0157ba-2c2d-49f0-ab61-2363ce18204a' WHERE institution_stammdaten.id = '480f3e81-59d0-4c2a-9f85-0f9aafbe18fc';
UPDATE institution_stammdaten set adresse_id = '8d08e0ea-56b6-4764-93c2-e4d945bbc021' WHERE institution_stammdaten.id = '70564bf2-793b-44ef-ab20-16660e4f7303';
UPDATE institution_stammdaten set adresse_id = 'f63e05b9-e066-4aec-8222-4e15f382fc70' WHERE institution_stammdaten.id = 'c10405d6-a905-4879-bb38-fca4cbb3f06f';
UPDATE institution_stammdaten set adresse_id = 'df293958-1125-4f78-bec3-97df287de9cd' WHERE institution_stammdaten.id = 'd01d7843-0ccb-48a0-ae9e-95bba502ecda';

# Anschliessend wird der FK hinzugefügt und die Spalte Adresse auf 'NOT NULL' gesetzt
ALTER TABLE institution_stammdaten
  ADD CONSTRAINT FK_institutionStammdaten_adresse_id
FOREIGN KEY (adresse_id)
REFERENCES adresse(id);

ALTER TABLE institution_stammdaten MODIFY adresse_id VARCHAR(36) NOT NULL;


# ----------------------------------------------------------------------------------------------------------------------
# In der Institutions Stammdaten sind gewisse daten nicht mehr plicht
# ----------------------------------------------------------------------------------------------------------------------

# Iban ist nicht mehr pflicht
ALTER TABLE institution_stammdaten MODIFY COLUMN iban VARCHAR(34) NULL;
# oeffnungsstunden ist nicht mehr pflicht
ALTER TABLE institution_stammdaten MODIFY COLUMN oeffnungsstunden DECIMAL(19, 2) NULL;
# oeffnungstage ist nicht mehr pflicht
ALTER TABLE institution_stammdaten MODIFY COLUMN oeffnungstage DECIMAL(19, 2) NULL;

