package ch.dvbern.ebegu.vorlagen.mahnung;

import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.enums.MahnungTyp;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrint;
import ch.dvbern.ebegu.vorlagen.AufzaehlungPrintImpl;
import ch.dvbern.ebegu.vorlagen.BriefPrintImpl;
import ch.dvbern.ebegu.vorlagen.PrintUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
public class ManhungPrintImpl extends BriefPrintImpl implements ManhungPrint {

	private Mahnung mahnung;
	private Mahnung vorgaengerMahnung;

	public ManhungPrintImpl(Mahnung mahnung, Optional<Mahnung> vorgaengerMahnung) {

		super(mahnung.getGesuch());
		this.mahnung = mahnung;

		if (mahnung.getMahnungTyp() == MahnungTyp.ZWEITE_MAHNUNG && vorgaengerMahnung.isPresent()) {
			this.vorgaengerMahnung = vorgaengerMahnung.get();
		} else if (mahnung.getMahnungTyp() == MahnungTyp.ZWEITE_MAHNUNG) {
			throw new UnsupportedOperationException("Vorganger Mahnung für zweite Mahnung fehlt!");
		}
	}

	@Override
	public String getAngebotFull() {

		List<String> angebotFull = new ArrayList<>();

		for (KindContainer kindContainer : getGesuch().getKindContainers()) {
			angebotFull.addAll(
				kindContainer.getBetreuungen().stream()
					.map(betreuung -> betreuung.getKind().getKindGS().getFullName() + " (" + betreuung.getInstitutionStammdaten().getInstitution().getName() + ")")
					.collect(Collectors.toList())
			);
		}

		return angebotFull.stream()
			.collect(Collectors.joining(", "));

	}

	@Override
	public String getAngebotShort() {

		String angebotShort = getGesuch().getKindContainers().stream()
			.map(kindContainer -> kindContainer.getKindGS().getFullName())
			.collect(Collectors.joining(", "));

		return angebotShort;

	}

	@Override
	public String getPeriode() {
		return "(" + getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigAb().getYear()
			+ "/" + getGesuch().getGesuchsperiode().getGueltigkeit().getGueltigBis().getYear() + ")";
	}

	@Override
	public String getFallNummer() {
		return PrintUtil.createFallNummerString(getGesuch());
	}

	@Override
	public String getFallDatum() {
		return Constants.DATE_FORMATTER.format(mahnung.getGesuch().getFall().getTimestampErstellt());
	}

	@Override
	public List<AufzaehlungPrint> getFehlendeUnterlagen() {

		List<AufzaehlungPrint> fehlendeUnterlagen = new ArrayList<>();

		String[] splitFehlendeUnterlagen = mahnung.getBemerkungen().split("[" + System.getProperty("line.separator") + "]+");
		for (String fehlendeUnterlage : splitFehlendeUnterlagen) {
			fehlendeUnterlagen.add(new AufzaehlungPrintImpl(fehlendeUnterlage));
		}
		return fehlendeUnterlagen;
	}

	@Override
	public String getMahnFristDatum() {
		return Constants.DATE_FORMATTER.format(mahnung.getDatumFristablauf());
	}

	@Override
	public String getErsteMahnDatum() {
		if (mahnung.getMahnungTyp() == MahnungTyp.ZWEITE_MAHNUNG && vorgaengerMahnung != null) {
			return Constants.DATE_FORMATTER.format(vorgaengerMahnung.getTimestampErstellt());
		} else {
			return "";
		}
	}

	@Override
	public String getKontaktStelle() {
		//TODO: this text should be dynamic and depends on a certain requirement being met
		return "Montag bis Donnerstag von 13.00–17.00 Uhr unter der Telefonnummer 031 321 51 15";
	}

}
