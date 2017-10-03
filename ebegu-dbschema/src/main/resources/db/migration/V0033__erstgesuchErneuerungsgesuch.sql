UPDATE gesuch
SET typ = 'ERSTGESUCH'
WHERE typ = 'GESUCH';
UPDATE gesuch_aud
SET typ = 'ERSTGESUCH'
WHERE typ = 'GESUCH';