package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.errors.MergeDocException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Optional;

/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 09.08.2016
*/
public interface PDFService {

	@Nonnull
	byte[] generateNichteintreten(Betreuung betreuung) throws MergeDocException;

	@Nonnull
	byte[] generateMahnung(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung) throws MergeDocException;

	@Nonnull
	byte[] generateFreigabequittung(Gesuch gesuch, Zustelladresse zustelladresse) throws MergeDocException;

	@Nonnull
	byte[] generateFinanzielleSituation(@Nonnull Gesuch gesuch, Verfuegung famGroessenVerfuegung) throws MergeDocException;

	@Nonnull
	byte[] generateVerfuegungForBetreuung(Betreuung betreuung, @Nullable LocalDate letzteVerfuegungDatum) throws MergeDocException;

}
