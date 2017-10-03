ALTER TABLE zahlungsauftrag add betrag_total_auftrag DECIMAL(19, 2);
ALTER TABLE zahlungsauftrag_aud add betrag_total_auftrag DECIMAL(19, 2);

ALTER TABLE zahlung add betrag_total_zahlung DECIMAL(19, 2);
ALTER TABLE zahlung_aud add betrag_total_zahlung DECIMAL(19, 2);