package ch.dvbern.ebegu.vorlagen.freigabequittung;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Zustelladresse;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.ServerMessageUtil;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import ch.dvbern.ebegu.vorlagen.PrintUtil;
import ch.dvbern.lib.doctemplate.docx.DocxImage;
import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
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

	public FreigabequittungPrintImpl(Gesuch gesuch, Zustelladresse zustellAmt) {

		super(gesuch);

		this.gesuch = gesuch;
		this.zustellAmt = zustellAmt;

	}

	@Override
	public DocxImage getBarcodeImage() throws IOException {

		DataMatrixBean dataMatrixBean = new DataMatrixBean();

		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();

		BitmapCanvasProvider canvas = new BitmapCanvasProvider(
			bytesOut, "image/x-png", 150, BufferedImage.TYPE_BYTE_BINARY, false, 0);

		dataMatrixBean.generateBarcode(canvas, "§FREIGABE|OPEN|" + gesuch.getAntragNummer() +"§");

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
	public String getPeriode() {
		return "(" + gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()
			+ "/" + gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis().getYear() + ")";
	}

	@Override
	public String getFallNummer() {
		return PrintUtil.createFallNummerString(getGesuch());
	}

	@Override
	public String getFallDatum() {
		return Constants.DATE_FORMATTER.format(gesuch.getFall().getTimestampErstellt());
	}

	@Override
	public String getAdresseGS1() {

		return getNameAdresseFormatiert(gesuch.getGesuchsteller1());

	}

	@Override
	public String getAdresseGS2() {

		return getNameAdresseFormatiert(gesuch.getGesuchsteller2());

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

	private String getNameAdresseFormatiert(Gesuchsteller gesuchsteller){

		if (gesuchsteller != null){
			String newlineMSWord = "\n";
			String adresse = StringUtils.EMPTY;

			adresse += gesuchsteller.getFullName();

			Optional<GesuchstellerAdresse> gsa = PrintUtil.getGesuchstellerAdresse(gesuchsteller);
			if (gsa.isPresent()) {
				if (StringUtils.isNotEmpty(gsa.get().getHausnummer())) {
					adresse += newlineMSWord + gsa.get().getStrasse() + " " + gsa.get().getHausnummer();
				} else {
					adresse += newlineMSWord + gsa.get().getStrasse();
				}
			}

			String adrZusatz = PrintUtil.getAdresszusatz(gesuch);
			if (StringUtils.isNotEmpty(adrZusatz)) {
				adresse += newlineMSWord + adrZusatz;
			}

			adresse += newlineMSWord + PrintUtil.getGesuchstellerPLZStadt(gesuch);

			return adresse;
		} else{
			return StringUtils.EMPTY;
		}

	}

	@Override
	public List<AufzaehlungPrint> getUnterlagen() {
		List<AufzaehlungPrint> aufzaehlungPrint = new ArrayList<>();

		Set<DokumentGrund> dokumentGrunden = gesuch.getDokumentGrunds();

		StringBuilder bemerkungenBuilder;
		if (dokumentGrunden != null) {
			for (DokumentGrund dokumentGrund : dokumentGrunden) {
                bemerkungenBuilder = new StringBuilder();

                if (dokumentGrund.isNeeded() && dokumentGrund.isEmpty()) {
                    bemerkungenBuilder.append((ServerMessageUtil.translateEnumValue(dokumentGrund.getDokumentTyp())));

                    if (StringUtils.isNotEmpty(dokumentGrund.getFullName())) {
                        bemerkungenBuilder.append(" (");
                        bemerkungenBuilder.append(dokumentGrund.getFullName());

                        if (dokumentGrund.getTag() != null) {
                            bemerkungenBuilder.append(" / ").append(dokumentGrund.getTag());
                        }
                        bemerkungenBuilder.append(")");
                    }

                }
                aufzaehlungPrint.add(new AufzaehlungPrintImpl(bemerkungenBuilder.toString()));
            }
		}
		return aufzaehlungPrint;
	}

}
