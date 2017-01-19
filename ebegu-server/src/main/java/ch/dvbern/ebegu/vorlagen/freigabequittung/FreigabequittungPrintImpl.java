package ch.dvbern.ebegu.vorlagen.freigabequittung;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.lib.doctemplate.docx.DocxImage;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 16/11/2016.
 */
public class FreigabequittungPrintImpl extends BriefPrintImpl implements FreigabequittungPrint {

	private Gesuch gesuch;
	private Zustelladresse zustellAmt;
	private List<DokumentGrund> dokumentGrunds;
	private List<AufzaehlungPrint> unterlagen;

	public FreigabequittungPrintImpl(Gesuch gesuch, Zustelladresse zustellAmt, List<DokumentGrund> dokumentGrunds) {

		super(gesuch);

		this.dokumentGrunds = dokumentGrunds;
		this.gesuch = gesuch;
		this.zustellAmt = zustellAmt;
		this.unterlagen = buildUnterlagen();

	}

	private List<AufzaehlungPrint> buildUnterlagen() {
		List<AufzaehlungPrint> aufzaehlungPrint = new ArrayList<>();
		StringBuilder bemerkungenBuilder;

		if (dokumentGrunds != null) {
			for (DokumentGrund dokumentGrund : dokumentGrunds) {
				bemerkungenBuilder = PrintUtil.parseDokumentGrundDataToString(dokumentGrund);
				if (bemerkungenBuilder.length() > 0){aufzaehlungPrint.add(new AufzaehlungPrintImpl(bemerkungenBuilder.toString()));}
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

		dataMatrixBean.generateBarcode(canvas, "§FREIGABE|OPEN|" + gesuch.getId() +"§");

		canvas.finish();

		BufferedImage bufferedImage = canvas.getBufferedImage();

		return new DocxImage(bytesOut.toByteArray(), bufferedImage.getWidth(), bufferedImage.getHeight(), DocxImage.Format.PNG);

	}

	@Override
	public boolean isAdresseJugendamt() {
		return zustellAmt == Zustelladresse.JUGENDAMT;
	}

	@Override
	public boolean isAdresseSchulamt() {
		return zustellAmt == Zustelladresse.SCHULAMT;
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

}
