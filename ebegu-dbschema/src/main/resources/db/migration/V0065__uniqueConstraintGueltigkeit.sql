/*Make gueltigkeit nullable*/
ALTER TABLE gesuch Modify gueltig BIT NULL DEFAULT NULL;

/*Set gueltig on Null where gueltig was false*/
UPDATE gesuch
SET gueltig=NULL
WHERE gueltig is FALSE;

/*Query to set only one gueltig on last verfuegtes Gesuch. This set guelitg to null on fall and gesuchsperiode where are not last verfuegt*/
UPDATE gesuch AS g
INNER JOIN
(
	SELECT
		fall_id AS fallid,
		gesuchsperiode_id AS gesuchsperiodeid,
		Max(timestamp_verfuegt) AS timestampverfuegtmax
	FROM gesuch AS SS
	WHERE gueltig = 1
	GROUP BY fall_id, gesuchsperiode_id
	HAVING count(id) > 1
)
	AS mtog
	ON g.fall_id = mtog.fallid AND g.gesuchsperiode_id = mtog.gesuchsperiodeid  AND g.timestamp_verfuegt != mtog.timestampverfuegtmax
SET g.gueltig = NULL;

/*Add Unique Contraint on fall gesuchsperiode and gesuchsperiode*/
ALTER TABLE gesuch
	ADD CONSTRAINT UK_gueltiges_gesuch UNIQUE (fall_id, gesuchsperiode_id, gueltig);