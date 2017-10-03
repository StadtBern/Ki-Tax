UPDATE verfuegung_zeitabschnitt
SET zahlungsstatus = 'NEU'
WHERE zahlungsstatus = 'IDENTISCH';
UPDATE verfuegung_zeitabschnitt_aud
SET zahlungsstatus = 'NEU'
WHERE zahlungsstatus = 'IDENTISCH';