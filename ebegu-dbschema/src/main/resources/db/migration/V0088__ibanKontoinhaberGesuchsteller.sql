ALTER TABLE gesuchsteller ADD COLUMN iban VARCHAR(34);
ALTER TABLE gesuchsteller_aud ADD COLUMN iban VARCHAR(34);

ALTER TABLE gesuchsteller ADD COLUMN kontoinhaber VARCHAR(255);
ALTER TABLE gesuchsteller_aud ADD COLUMN kontoinhaber VARCHAR(255);