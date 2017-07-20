-- Eine Liste generieren, mit allen Fallnummern, wo es ein Kind gibt mit einem halben Abzug --

## ALL
SELECT fall.fall_nummer
FROM fall
LEFT JOIN gesuch ON fall.id = gesuch.fall_id
LEFT JOIN kind_container ON gesuch.id = kind_container.gesuch_id
LEFT JOIN kind ON kind_container.kindja_id = kind.id
WHERE kind.kinderabzug = 'HALBER_ABZUG'
GROUP BY fall.fall_nummer
ORDER BY fall.fall_nummer;

## NOT(ERSTGESUCH in IN_BEARBEITUNG_GS)   <<------ NUR diese Liste ist fuers JA interessant. Da die Erstgesuche im
## Status IN_BEARBEITUNG_GS nicht aufgelistet werden
SELECT fall.fall_nummer
FROM fall
LEFT JOIN gesuch ON fall.id = gesuch.fall_id
LEFT JOIN kind_container ON gesuch.id = kind_container.gesuch_id
LEFT JOIN kind ON kind_container.kindja_id = kind.id
WHERE kind.kinderabzug = 'HALBER_ABZUG' AND NOT (gesuch.status =
												 'IN_BEARBEITUNG_GS' AND gesuch.typ = 'ERSTGESUCH')
GROUP BY fall.fall_nummer
ORDER BY fall.fall_nummer;

## ERSTGESUCH in IN_BEARBEITUNG_GS
SELECT fall.fall_nummer
FROM fall
LEFT JOIN gesuch ON fall.id = gesuch.fall_id
LEFT JOIN kind_container ON gesuch.id = kind_container.gesuch_id
LEFT JOIN kind ON kind_container.kindja_id = kind.id
WHERE kind.kinderabzug = 'HALBER_ABZUG' AND gesuch.status =
											'IN_BEARBEITUNG_GS' AND gesuch.typ = 'ERSTGESUCH'
GROUP BY fall.fall_nummer
ORDER BY fall.fall_nummer;