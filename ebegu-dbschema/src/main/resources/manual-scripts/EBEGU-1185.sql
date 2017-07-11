-- Eine Liste generieren, mit allen Fallnummern, wo es ein Kind gibt mit einem halben Abzug --

SELECT fall.fall_nummer
FROM fall
  LEFT JOIN gesuch ON fall.id = gesuch.fall_id
  LEFT JOIN kind_container ON gesuch.id = kind_container.gesuch_id
  LEFT JOIN kind ON kind_container.kindja_id = kind.id WHERE kind.kinderabzug = 'HALBER_ABZUG'
GROUP BY fall.id
ORDER BY fall.fall_nummer;