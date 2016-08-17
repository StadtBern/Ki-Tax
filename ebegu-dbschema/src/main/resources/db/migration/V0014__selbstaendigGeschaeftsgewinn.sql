# selbstaendig
ALTER TABLE finanzielle_situation
  DROP COLUMN selbstaendig;

ALTER TABLE finanzielle_situation_aud
  DROP COLUMN selbstaendig;

ALTER TABLE einkommensverschlechterung
  DROP COLUMN selbstaendig;

ALTER TABLE einkommensverschlechterung_aud
  DROP COLUMN selbstaendig;

# geschaefstgewinn
ALTER TABLE einkommensverschlechterung
  DROP COLUMN geschaeftsgewinn_basisjahr_minus1;

ALTER TABLE einkommensverschlechterung
  DROP COLUMN geschaeftsgewinn_basisjahr_minus2;

ALTER TABLE einkommensverschlechterung_aud
  DROP COLUMN geschaeftsgewinn_basisjahr_minus1;

ALTER TABLE einkommensverschlechterung_aud
  DROP COLUMN geschaeftsgewinn_basisjahr_minus2;
