package ch.dvbern.ebegu.tests;

import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.services.EbeguVorlageServiceBean;
import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Optional;


/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 21/11/2016.
 */
public class EbeguVorlageServiceMock extends EbeguVorlageServiceBean{

	@Nonnull
	@Override
	public Optional<EbeguVorlage> getEbeguVorlageByDatesAndKey(LocalDate abDate, LocalDate bisDate, EbeguVorlageKey ebeguVorlageKey, EntityManager em) {
		EbeguVorlage ebeguVorlage = new EbeguVorlage(ebeguVorlageKey, new DateRange(abDate, bisDate));
		Vorlage vorlage = new Vorlage();

		switch (ebeguVorlageKey) {
			case VORLAGE_MAHNUNG_1:
				vorlage.setFilepfad("vorlagen/1_Mahnung.docx");
				break;
			case VORLAGE_MAHNUNG_2:
				vorlage.setFilepfad("vorlagen/2_Mahnung.docx");
				break;
			case VORLAGE_NICHT_EINTRETENSVERFUEGUNG:
				vorlage.setFilepfad("vorlage/Nichteintretensverfuegung.docx");
				break;
			case VORLAGE_INFOSCHREIBEN_MAXIMALTARIF:
				vorlage.setFilepfad("vorlage/Infoschreiben_Maxtarif.docx");
				break;
			default:
				break;
		}

		ebeguVorlage.setVorlage(vorlage);

		return Optional.of(ebeguVorlage);
	}
}
