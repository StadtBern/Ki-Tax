package ch.dvbern.ebegu.vorlagen.verfuegung;
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

import ch.dvbern.ebegu.vorlagen.EBEGUMergeSource;
import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

import java.util.List;

public class VerfuegungPrintMergeSource implements EBEGUMergeSource {

	private VerfuegungPrint verfuegung;
	private boolean isPDFLongerThanExpected = false;

	/**
	 * @param verfuegung
	 */
	public VerfuegungPrintMergeSource(VerfuegungPrint verfuegung) {
		this.verfuegung = verfuegung;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("verfuegung")) {
			return new BeanMergeSource(verfuegung, "verfuegung.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		if (key.equals("verfuegung.PDFLongerThanExpected")) {
			return isPDFLongerThanExpected;
		}
		return new BeanMergeSource(verfuegung, "verfuegung.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		// comment homa: Ich haette jetzt hier evtl auch "verfuegung" als prefix genommen oder aber "betreuungen" klein
		// geschrieben
		// ist aber muehsam zu fixen daher lasse ichs mal so
		if (key.startsWith("Betreuungen")) {
			return new BeanMergeSource(verfuegung, "Betreuungen.").whileStatement(mergeContext, key);
		}
		if (key.startsWith("Bemerkungen")) {
			return new BeanMergeSource(verfuegung, "Bemerkungen.").whileStatement(mergeContext, key);
		}
		return null;
	}

	@Override
	public void setPDFLongerThanExpected(boolean isPDFLongerThanExpected) {
		this.isPDFLongerThanExpected = isPDFLongerThanExpected;
	}
}
