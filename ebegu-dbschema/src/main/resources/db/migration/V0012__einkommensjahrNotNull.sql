UPDATE verfuegung_zeitabschnitt SET einkommensjahr = 2015 WHERE einkommensjahr IS NULL;
ALTER TABLE verfuegung_zeitabschnitt MODIFY einkommensjahr INT(11) NOT NULL;