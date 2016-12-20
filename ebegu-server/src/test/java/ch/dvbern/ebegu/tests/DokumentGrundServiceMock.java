package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.services.DokumentGrundServiceBean;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 06/12/2016.
 */
public class DokumentGrundServiceMock extends DokumentGrundServiceBean {

	@Nonnull
	@Override
	public Collection<DokumentGrund> findAllDokumentGrundByGesuch(@Nonnull Gesuch gesuch) {
		return Collections.emptyList();
	}
}
