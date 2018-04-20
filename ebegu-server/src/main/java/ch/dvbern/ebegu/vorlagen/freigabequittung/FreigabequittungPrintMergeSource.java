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

package ch.dvbern.ebegu.vorlagen.freigabequittung;

import java.io.IOException;
import java.util.List;

import ch.dvbern.ebegu.vorlagen.EBEGUMergeSource;
import ch.dvbern.lib.doctemplate.common.BeanMergeSource;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.common.MergeContext;
import ch.dvbern.lib.doctemplate.common.MergeSource;

public class FreigabequittungPrintMergeSource implements EBEGUMergeSource {

	private boolean isPDFLongerThanExpected = false;
	private final FreigabequittungPrint quittung;

	public FreigabequittungPrintMergeSource(FreigabequittungPrint quittung) {
		this.quittung = quittung;
	}

	@Override
	public Object getData(MergeContext mergeContext, String key) throws DocTemplateException {

		if (key.startsWith("barcodeImage")) {
			try {
				return quittung.getBarcodeImage();
			} catch (IOException e) {
				throw new DocTemplateException("Fehler beim Strichcode generieren für Freigabequittung", e);
			}
		}

		if (key.startsWith("printMerge")) {
			return new BeanMergeSource(quittung, "printMerge.").getData(mergeContext, key);
		}

		return null;
	}

	@Override
	public Boolean ifStatement(MergeContext mergeContext, String key) throws DocTemplateException {
		if (key.equals("printMerge.PDFLongerThanExpected")) {
			return isPDFLongerThanExpected;
		}
		return new BeanMergeSource(quittung, "printMerge.").ifStatement(mergeContext, key);
	}

	@Override
	public List<MergeSource> whileStatement(MergeContext mergeContext, String key) throws DocTemplateException {

		String[] array = key.split("[.]+");
		String subkey = array[0];

		if (subkey.equalsIgnoreCase("printMerge")) {
			return new BeanMergeSource(quittung, "printMerge.").whileStatement(mergeContext, key);
		}

		if (subkey.equalsIgnoreCase("betreuungsTabelle")) {
			return new BeanMergeSource(quittung, "betreuungsTabelle.").whileStatement(mergeContext, key);
		}

		return null;

	}

	@Override
	public void setPDFLongerThanExpected(boolean isPDFLongerThanExpected) {
		this.isPDFLongerThanExpected = isPDFLongerThanExpected;
	}
}
