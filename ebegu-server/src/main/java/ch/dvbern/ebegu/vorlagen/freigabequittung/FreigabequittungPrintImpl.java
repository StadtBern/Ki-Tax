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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.lib.doctemplate.docx.DocxImage;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

public class FreigabequittungPrintImpl extends BriefPrintImpl implements FreigabequittungPrint {

	private final Gesuch gesuch;
	private final List<DokumentGrund> dokumentGrunds;
	private final List<AufzaehlungPrint> unterlagen;

	public FreigabequittungPrintImpl(Gesuch gesuch, List<DokumentGrund> dokumentGrunds) {

		super(gesuch);

		this.dokumentGrunds = dokumentGrunds;
		this.gesuch = gesuch;
		this.unterlagen = buildUnterlagen();

	}

	private List<AufzaehlungPrint> buildUnterlagen() {
		List<AufzaehlungPrint> aufzaehlungPrint = new ArrayList<>();
		StringBuilder bemerkungenBuilder;

		if (dokumentGrunds != null) {
			for (DokumentGrund dokumentGrund : dokumentGrunds) {
				bemerkungenBuilder = PrintUtil.parseDokumentGrundDataToString(dokumentGrund);
				if (bemerkungenBuilder.length() > 0) {
					aufzaehlungPrint.add(new AufzaehlungPrintImpl(bemerkungenBuilder.toString()));
				}
			}
		}

		return aufzaehlungPrint;
	}

	@Override
	public DocxImage getBarcodeImage() throws IOException {

		DataMatrixBean dataMatrixBean = new DataMatrixBean();

		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

		BitmapCanvasProvider canvas = new BitmapCanvasProvider(
			bytesOut, "image/x-png", 175, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		dataMatrixBean.generateBarcode(canvas, "§FREIGABE|OPEN|" + gesuch.getId() + "§");

		canvas.finish();

		BufferedImage bufferedImage = canvas.getBufferedImage();

		return new DocxImage(bytesOut.toByteArray(), bufferedImage.getWidth(), bufferedImage.getHeight(), DocxImage.Format.PNG);

	}

	@Override
	public String getAdresseGS1() {

		return PrintUtil.getNameAdresseFormatiert(gesuch, gesuch.getGesuchsteller1());

	}

	@Override
	public boolean isAddresseGS2() {
		return gesuch.getGesuchsteller2() != null;
	}

	@Override
	public String getAdresseGS2() {

		return PrintUtil.getNameAdresseFormatiert(gesuch, gesuch.getGesuchsteller2());

	}

	@Override
	public List<BetreuungsTabellePrint> getBetreuungen() {

		Set<Betreuung> betreuungen = new TreeSet<>();

		for (KindContainer kindContainer : gesuch.getKindContainers()) {
			betreuungen.addAll(kindContainer.getBetreuungen());
		}

		return betreuungen.stream()
			.map(BetreuungsTabellePrintImpl::new)
			.collect(Collectors.toList());

	}

	@Override
	public List<AufzaehlungPrint> getUnterlagen() {
		return this.unterlagen;
	}

	@Override
	public boolean isWithoutUnterlagen() {
		return this.unterlagen == null || this.unterlagen.isEmpty();
	}

	@Nullable
	@Override
	public String getFristverlaengerung() {
		if (gesuch.hasOnlyBetreuungenOfSchulamt()) {
			if (gesuch.getFristverlaengerung() == null) {
				return ServerMessageUtil.getMessage("Freigabequittung_Keine_Fristverlaengerung");
			}
			return Constants.DATE_FORMATTER.format(gesuch.getFristverlaengerung());
		}
		return null;
	}

	@Nullable
	@Override
	public String getFristverlaengerungTitle() {
		if (gesuch.hasOnlyBetreuungenOfSchulamt()) {
			return ServerMessageUtil.getMessage("Freigabequittung_Fristverlaengerung_Title");
		}
		return null;
	}
}
