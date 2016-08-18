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
import java.util.stream.Collectors;

import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

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

		// Diese Konstanten werden in der Wordvolage verwendet. Bei Aenderungen muss Wordvorlage angepasst werden
		if ("gesuchstellerStrasse".equalsIgnoreCase(key)) {
			return verfuegung.getGesuchstellerStrasse();
		} else if ("gesuchstellerPlzStadt".equalsIgnoreCase(key)) {
			return verfuegung.getGesuchstellerPLZStadt();
		} else if ("referenznummer".equalsIgnoreCase(key)) {
			return verfuegung.getReferenzNummer();
		} else if ("verfuegungsdatum".equalsIgnoreCase(key)) {
			return verfuegung.getVerfuegungsdatum();
		} else if ("gesuchsteller1".equalsIgnoreCase(key)) {
			return verfuegung.getGesuchsteller1();
		} else if ("gesuchsteller2".equalsIgnoreCase(key)) {
			return verfuegung.getGesuchsteller2();
		} else if ("kindNameVorname".equalsIgnoreCase(key)) {
			return verfuegung.getKindNameVorname();
		} else if ("kindGeburtsdatum".equalsIgnoreCase(key)) {
			return verfuegung.getKindGeburtsdatum();
		} else if ("kitabezeichnung".equalsIgnoreCase(key)) {
			return verfuegung.getKitaBezeichnung();
		} else if ("anspruchAb".equalsIgnoreCase(key)) {
			return verfuegung.getAnspruchAb();
		} else if ("anspruchBis".equalsIgnoreCase(key)) {
			return verfuegung.getAnspruchBis();
		} else if ("bemerkungen".equalsIgnoreCase(key)) {
			return verfuegung.getBemerkungen();
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if ("Mutation".equalsIgnoreCase(key)) {
			return verfuegung.isMutation();
		}
		if ("gesuchsteller2Exist".equalsIgnoreCase(key)) {
			return verfuegung.existGesuchsteller2();
		}
		if ("PensumIstGroesser0".equalsIgnoreCase(key)) {
			return verfuegung.isPensumGrosser0();
		}
		if ("pensumIst0".equalsIgnoreCase(key)) {
			return !verfuegung.isPensumGrosser0();
		}

		return Boolean.valueOf(true);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("Betreuungen")) {
			return verfuegung.getVerfuegungZeitabschnitt().stream().map(verfuegungZeitabschnitt -> new ZeitabschnittPrintMergeSource(verfuegungZeitabschnitt))
					.collect(Collectors.toList());
		}
		return null;
	}
}
