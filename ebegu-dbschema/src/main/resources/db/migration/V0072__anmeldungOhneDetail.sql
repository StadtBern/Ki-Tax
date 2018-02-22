ALTER TABLE betreuung ADD keine_detailinformationen BIT;
ALTER TABLE betreuung_aud ADD keine_detailinformationen BIT;

update betreuung set keine_detailinformationen = false;