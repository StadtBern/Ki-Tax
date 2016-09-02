package ch.dvbern.ebegu.vorlagen;
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

import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

import java.util.List;

public class FinanzielleSituationEinkommensverschlechterungPrintMergeSource implements MergeSource {

	private BerechnungsgrundlagenInformationPrint berechnung;

	/**
	 * @param berechnungsgrundlagenFinanziellenSituationPrint
	 */
	public FinanzielleSituationEinkommensverschlechterungPrintMergeSource(BerechnungsgrundlagenInformationPrint berechnung) {
		this.berechnung = berechnung;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("berechnung")) {
			return new BeanMergeSource(berechnung, "berechnung.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		return new BeanMergeSource(berechnung, "berechnung.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("berechnung")) {
			return new BeanMergeSource(berechnung, "berechnung.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
