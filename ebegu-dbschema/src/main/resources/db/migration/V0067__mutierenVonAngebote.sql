/*Add Mutationszustand der Anmeldung (Zustand  "noch nicht freigegeben", "Mutiert", "Aktuelle Anmeldung" und null)*/
ALTER TABLE betreuung ADD anmeldung_mutation_zustand varchar(255);

ALTER TABLE betreuung_aud ADD anmeldung_mutation_zustand varchar(255);