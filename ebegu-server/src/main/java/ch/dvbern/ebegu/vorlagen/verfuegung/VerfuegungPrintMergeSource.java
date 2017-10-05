/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.vorlagen.verfuegung;

import java.util.List;

import ch.dvbern.ebegu.vorlagen.EBEGUMergeSource;
import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

public class VerfuegungPrintMergeSource implements EBEGUMergeSource {

	private final VerfuegungPrint verfuegung;
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
