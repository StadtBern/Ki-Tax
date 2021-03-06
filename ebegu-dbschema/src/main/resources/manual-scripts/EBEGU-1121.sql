-- Dokument ermitteln:
select * from dokument where dokument_grund_id = (select id from dokument_grund where dokument_grund_typ = 'SONSTIGE_NACHWEISE' and gesuch_id = (select g.id from gesuch g LEFT JOIN fall f on g.fall_id = f.id where f.fall_nummer = 1373));

-- Resultat:
-- +--------------------------------------+---------------------+---------------------+---------------+--------------+---------+---------------+----------------------+--------------------------------------------------------------------------------------------+----------+--------------------------------------+
-- | id                                   | timestamp_erstellt  | timestamp_mutiert   | user_erstellt | user_mutiert | version | vorgaenger_id | filename             | filepfad                                                                                   | filesize | dokument_grund_id                    |
-- +--------------------------------------+---------------------+---------------------+---------------+--------------+---------+---------------+----------------------+--------------------------------------------------------------------------------------------+----------+--------------------------------------+
-- | ccc4c880-11aa-42a0-b359-6961d9d1f855 | 2017-05-29 11:27:25 | 2017-05-29 11:27:25 | JAJSC1        | JAJSC1       |       0 | NULL          | Freigabequittung.pdf | /media/ebegu/721a2d65-b4b7-4937-9de5-6a4586a4b4db/33ba41e8-213d-41bc-9ccf-b2860aaafe38.pdf | 227.5 kB | c6ae8159-63f4-4b06-b3d6-8a77e4ed9be1 |
-- | ea4b930e-9c1d-4297-9fa5-8df6ac5661bd | 2017-05-29 11:47:48 | 2017-05-29 11:47:48 | JAJSC1        | JAJSC1       |       0 | NULL          | Freigabequittung.pdf | /media/ebegu/721a2d65-b4b7-4937-9de5-6a4586a4b4db/59f2863d-e09e-429d-af69-51b016b8475b.pdf | 133.6 kB | c6ae8159-63f4-4b06-b3d6-8a77e4ed9be1 |
-- +--------------------------------------+---------------------+---------------------+---------------+--------------+---------+---------------+----------------------+--------------------------------------------------------------------------------------------+----------+--------------------------------------+

-- Dokument loeschen:
delete from dokument where id = 'ea4b930e-9c1d-4297-9fa5-8df6ac5661bd';