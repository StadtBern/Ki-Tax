package ch.dvbern.ebegu;

import ch.dvbern.ebegu.entities.Adresse;

import java.time.LocalDate;

/**
 * User: homa
 * Date: 21.03.16
 * comments homa
 */
public class TestDataUtil {


	private TestDataUtil(){

	}

	public  static Adresse createDefaultAdresse() {
		Adresse adresse = new Adresse();
		adresse.setStrasse("Nussbaumstrasse ");
		adresse.setHausnummer("21");
		adresse.setPlz("3014");
		adresse.setOrt("Bern");
		adresse.setGueltigAb(LocalDate.now());
		adresse.setGueltigAb(LocalDate.now().plusMonths(1));
		LocalDate now = LocalDate.now();
		adresse.setGueltigAb(now);
		adresse.setGueltigBis(now);
		return adresse;
	}
}
