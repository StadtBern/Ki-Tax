alter table gesuchsperiode
	add constraint UK_gesuchsperiode unique (gueltig_ab, gueltig_bis);
