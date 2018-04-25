-- Alle zuviel erstellten Zahlungspositionen loeschen

-- MariaDB [ebegu]> select count(*), zahlung_id, verfuegung_zeitabschnitt_id, betrag from zahlungsposition GROUP BY zahlung_id, verfuegung_zeitabschnitt_id, betrag having count(*) > 1;
-- +----------+--------------------------------------+--------------------------------------+----------+
-- | count(*) | zahlung_id                           | verfuegung_zeitabschnitt_id          | betrag   |
-- +----------+--------------------------------------+--------------------------------------+----------+
-- |        2 | 4185b2e5-ecf7-47ae-93ab-d0eeda9622fe | a80a51c7-0f47-4c6e-bcd1-a44ccf5a6d34 |  -137.00 |
-- |        2 | 47cc9a8f-ca70-487c-840e-4341b5989859 | 60f3aca2-c632-4ea3-90f3-28e07ebad95c |  -587.95 |
-- |        2 | 47cc9a8f-ca70-487c-840e-4341b5989859 | 775e6f8f-1c9d-4ce9-9543-3b6f05da32cc |  -587.95 |
-- |        3 | 5a3a8c0e-4fae-4bba-8a1e-e6baae46e25a | 028dddfa-4199-4512-b2cf-4848a6e918d0 |  -172.50 |
-- |        2 | 5a3a8c0e-4fae-4bba-8a1e-e6baae46e25a | 4a827d07-d24c-46ec-9103-25725fbb2d4e |     0.00 |
-- |        2 | 5a3a8c0e-4fae-4bba-8a1e-e6baae46e25a | a197a243-0f56-4766-a162-991dfc83ff1c |     0.00 |
-- |        2 | 5a3a8c0e-4fae-4bba-8a1e-e6baae46e25a | d81b5ed9-55d0-43e2-a02c-7ad22a43c255 |     0.00 |
-- |        2 | 680a5565-fd44-4c86-83ba-caabe0c1c1db | 26858763-d2d6-4463-9652-d765f5d014a5 |  -152.35 |
-- |        2 | 680a5565-fd44-4c86-83ba-caabe0c1c1db | d0f8b5c8-e48d-4535-9d1e-f026c1fe4d19 |  -152.35 |
-- |        2 | 7086cafb-883b-433b-a6d1-ce94587e82d5 | 286864e6-72e3-4323-8d2a-74e26cde0ff0 | -1713.85 |
-- |        2 | bb0bc425-be86-449c-8e29-58ee257a4f16 | 23f46898-e9e7-495e-a7a2-9dc0aaefbd80 |  -175.30 |
-- |        2 | bb0bc425-be86-449c-8e29-58ee257a4f16 | b2c2ec70-7d71-44cd-b422-f81212e64ace |  -175.30 |
-- +----------+--------------------------------------+--------------------------------------+----------+
-- 12 rows in set (0.04 sec)

delete from zahlungsposition where id in ('58802c0a-24e0-4a04-aa5e-7ec581de324b', 'a4debba1-4348-4b08-914b-bdd70c0dc7c0',
'05f9c146-4bd1-4b09-9cc6-8d3ebb315cad', '880e2cdd-70be-44d6-b738-c399fa5f2cfb', '8db667ca-8123-40aa-9e76-d4f7a92047e3',
'32cc0eb0-d33a-4fb4-8c3f-cdd6a0b4e241', '30dd270c-5765-4acc-9385-3fd8ec03d83d', '9957f41d-de85-4806-8888-da95e298387e',
'1f44ae89-40db-4ee6-b55e-5a5ca175ffad', 'a577155b-8ee4-4fef-a6c3-607266431138', '8a07bc66-8616-4508-b372-6dac13fbb7d4',
'2936a254-a974-40d7-8da1-68932c03b0f2', '2055eeba-86d7-4c14-8332-6394eb74724b');

-- Die korrigierten Verfuegungszeitabschnitte auf VERRECHNET_KORRIGIERT setzen
-- bei allen zahlungspositionen mit status "KORREKTUR_VOLLKOSTEN" oder "KORREKTUR_ELTERNBEITRAG" AND ignoriert = false muss der dazugehoerige verfuegungszeigabschnitt auf VERRECHNET_KORRIGIERT gesetzt werden (später)

-- Die betroffenen ermitteln:
select * from verfuegung_zeitabschnitt where id in (
	SELECT verfuegung_zeitabschnitt_id
	FROM zahlungsposition
	WHERE status IN ('KORREKTUR_VOLLKOSTEN', 'KORREKTUR_ELTERNBEITRAG') AND ignoriert = FALSE
) and zahlungsstatus = 'VERRECHNET';

-- und updaten:
update verfuegung_zeitabschnitt set zahlungsstatus = 'VERRECHNET_KORRIGIERT' where id in (
	SELECT verfuegung_zeitabschnitt_id
	FROM zahlungsposition
	WHERE status IN ('KORREKTUR_VOLLKOSTEN', 'KORREKTUR_ELTERNBEITRAG') AND ignoriert = FALSE
) and zahlungsstatus = 'VERRECHNET';