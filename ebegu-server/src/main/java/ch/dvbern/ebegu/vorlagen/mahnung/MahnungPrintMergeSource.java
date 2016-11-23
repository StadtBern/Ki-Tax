package ch.dvbern.ebegu.vorlagen.mahnung;
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

public class MahnungPrintMergeSource implements MergeSource {

	private ManhungPrint mahnung;

	public MahnungPrintMergeSource(ManhungPrint mahnung) {
		this.mahnung = mahnung;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("mahnung")) {
			return new BeanMergeSource(mahnung, "mahnung.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		return new BeanMergeSource(mahnung, "mahnung.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		if (key.startsWith("mahnung")) {
			return new BeanMergeSource(mahnung, "mahnung.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
