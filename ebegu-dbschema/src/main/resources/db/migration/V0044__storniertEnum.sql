UPDATE betreuung
SET betreuungsstatus = 'STORNIERT'
WHERE betreuungsstatus = 'GEKUENDIGT_VOR_EINTRITT';
