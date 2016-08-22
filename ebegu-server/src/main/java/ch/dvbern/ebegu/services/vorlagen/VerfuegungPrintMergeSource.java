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

import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class VerfuegungPrintMergeSource implements MergeSource {

	private VerfuegungPrintDTO verfuegung;

	/**
	 * @param verfuegung
	 */
	public VerfuegungPrintMergeSource(VerfuegungPrintDTO verfuegung) {
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

		// TODO ZEAB new BeanMergeSource(verfuegung, "verfuegung.").ifStatement(mergeContext, key); wieder einschalten
		// und anpassen
		if ("verfuegung.Mutation".equalsIgnoreCase(key)) {
			return verfuegung.isMutation();
		} else if ("verfuegung.gesuchsteller2Exist".equalsIgnoreCase(key)) {
			return verfuegung.existGesuchsteller2();
		} else if ("verfuegung.PensumIstGroesser0".equalsIgnoreCase(key)) {
			return verfuegung.isPensumGrosser0();
		} else if ("verfuegung.pensumIst0".equalsIgnoreCase(key)) {
			return !verfuegung.isPensumGrosser0();
		} else if ("verfuegung.printbemerkung".equalsIgnoreCase(key)) {
			return StringUtils.isNotEmpty(verfuegung.getBemerkungen().trim());
		}
		return Boolean.FALSE;// new BeanMergeSource(verfuegung, "verfuegung.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("Betreuungen")) {
			return new BeanMergeSource(verfuegung, "Betreuungen.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
