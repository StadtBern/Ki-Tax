package ch.dvbern.ebegu.vorlagen.nichteintreten;

import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

import java.util.List;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 28/11/2016.
 */
public class NichteintretenPrintMergeSource implements MergeSource {

	private NichteintretenPrint nichteintreten;

	public NichteintretenPrintMergeSource(NichteintretenPrint nichteintreten) {
		this.nichteintreten = nichteintreten;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("printMerge")) {
			return new BeanMergeSource(nichteintreten, "printMerge.").getData(mergeContext, key);
		}
		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		return new BeanMergeSource(nichteintreten, "printMerge.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		if (key.startsWith("printMerge")) {
			return new BeanMergeSource(nichteintreten, "printMerge.").whileStatement(mergeContext, key);
		}
		return null;
	}
}
