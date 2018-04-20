ALTER TABLE ebegu_parameter
	ADD CONSTRAINT UK_ebegu_parameter UNIQUE (name, gueltig_ab, gueltig_bis);