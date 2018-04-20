select id from gesuch g LEFT JOIN  fall f ON g.fall_id = f.id where f.fall_nummer = 1495;
-- Gesuch ID: 148d767f-e720-4b8a-b786-093db5b53158

-- Gesuch geprueft am 30.05.2017, 15:05
-- Gesuch verfuegen am 30.05.2017 15:05:59
-- Gesuch verfuegt am 30.05.2017, 15:06

select * from generated_dokument where gesuch_id = '148d767f-e720-4b8a-b786-093db5b53158' and typ = 'FINANZIELLE_SITUATION';

-- Dokument umbenennen und ueber die Applikation neu erstellen
update generated_dokument set filepfad = '/media/ebegu/148d767f-e720-4b8a-b786-093db5b53158/eb10032d-d9c4-4cd1-91ce-0c9d29d3402a_old.pdf' where id = 'db3b8b7f-29a5-4b91-b2b0-e48f5af55373';
