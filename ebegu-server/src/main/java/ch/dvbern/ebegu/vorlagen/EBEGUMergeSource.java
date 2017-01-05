package ch.dvbern.ebegu.vorlagen;

import ch.dvbern.lib.doctemplate.common.MergeSource;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 29/12/2016.
 */
public interface EBEGUMergeSource extends MergeSource {

	void setPDFLongerThanExpected(boolean isPDFLongerThanExpected);

}
