ALTER TABLE zahlungsauftrag
	ADD betrag_total_auftrag DECIMAL(19, 2);
ALTER TABLE zahlungsauftrag_aud
	ADD betrag_total_auftrag DECIMAL(19, 2);

ALTER TABLE zahlung
	ADD betrag_total_zahlung DECIMAL(19, 2);
ALTER TABLE zahlung_aud
	ADD betrag_total_zahlung DECIMAL(19, 2);