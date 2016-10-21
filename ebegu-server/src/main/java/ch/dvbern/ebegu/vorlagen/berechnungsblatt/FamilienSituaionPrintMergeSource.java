package ch.dvbern.ebegu.vorlagen.berechnungsblatt;
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

import ch.dvbern.ebegu.vorlagen.finanziellesituation.FinanzielleSituationEinkommensverschlechterungPrintMergeSource;
import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

/**
 * @deprecated stattdessen wird die {@link FinanzielleSituationEinkommensverschlechterungPrintMergeSource} benutzt
 */
@Deprecated
public class FamilienSituaionPrintMergeSource implements MergeSource {

	private FamilienSituaionPrint familienSituaion;

	/**
	 * @param familienSituaionPrint
	 */
	public FamilienSituaionPrintMergeSource(FamilienSituaionPrint familienSituaionPrint) {
		this.familienSituaion = familienSituaionPrint;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("familienSituaion")) {
			return new BeanMergeSource(familienSituaion, "familienSituaion.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		return new BeanMergeSource(familienSituaion, "familienSituaion.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("berechnungen")) {
			return new BeanMergeSource(familienSituaion, "berechnungen.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
