package ch.dvbern.ebegu.services.vorlagen;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 12.08.2016
*/

import java.util.List;

import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

public class ZeitabschnittPrintMergeSource implements MergeSource {

	private VerfuegungZeitabschnittPrintDTO verfuegungZeitabschnitt;

	public ZeitabschnittPrintMergeSource(VerfuegungZeitabschnittPrintDTO verfuegungZeitabschnitt) {

		this.verfuegungZeitabschnitt = verfuegungZeitabschnitt;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		// Diese Konstanten werden in der Wordvolage verwendet. Bei Aenderungen muss den Wordvorlage angepasst werden
		if ("von".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getVon();
		} else if ("bis".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getBis();
		} else if ("betreuung".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getBetreuung();
		} else if ("anspruch".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getAnspruch();
		} else if ("bgpensum".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getBGPensum();
		} else if ("vollkosten".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getVollkosten();
		} else if ("elternbeitrag".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getElternbeitrag();
		} else if ("verguenstigung".equalsIgnoreCase(key)) {
			return verfuegungZeitabschnitt.getVerguenstigung();
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String s) throws DocTemplateException {

		return Boolean.FALSE;
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String s) throws DocTemplateException {

		return null;
	}
}
