ALTER TABLE betreuung
	ADD abwesenheit_mutiert BIT;
ALTER TABLE betreuung_aud
	ADD abwesenheit_mutiert BIT;

ALTER TABLE betreuung
	ADD betreuung_mutiert BIT;
ALTER TABLE betreuung_aud
	ADD betreuung_mutiert BIT;

ALTER TABLE kind_container
	ADD kind_mutiert BIT;
ALTER TABLE kind_container_aud
	ADD kind_mutiert BIT;