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

import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

public class VerfuegungsmusterMergeSource implements MergeSource {

	private Verfuegungsmuster verfuegungsmuster;

	/**
	 * @param verfuegungsmuster
	 */
	public VerfuegungsmusterMergeSource(Verfuegungsmuster verfuegungsmuster) {
		this.verfuegungsmuster = verfuegungsmuster;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("verfuegungsmuster")) {
			return new BeanMergeSource(verfuegungsmuster, "verfuegungsmuster.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if ("Mutation".equalsIgnoreCase(key)) {
			return new Boolean(verfuegungsmuster.isMutation());
		}
		if ("gesuchsteller2Exist".equalsIgnoreCase(key)) {
			return new Boolean(verfuegungsmuster.existGesuchsteller2());
		}
		if ("PensumIstGroesser0".equalsIgnoreCase(key)) {
			return new Boolean(verfuegungsmuster.isPensumGrosser0());
		}
		if ("pensumIst0".equalsIgnoreCase(key)) {
			return new Boolean(!verfuegungsmuster.isPensumGrosser0());
		}

		return Boolean.FALSE;
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("Betreuungen")) {
			return new BeanMergeSource(verfuegungsmuster, "Betreuungen.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
