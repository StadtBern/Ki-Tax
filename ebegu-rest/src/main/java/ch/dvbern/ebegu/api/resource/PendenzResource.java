package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxPendenzJA;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.services.GesuchService;
import io.swagger.annotations.Api;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * REST Resource fuer Pendenzen
 */
@Path("pendenzen")
@Stateless
@Api
public class PendenzResource {

	@Inject
	private JaxBConverter converter;
	@Inject
	private GesuchService gesuchService;

	/**
	 * Gibt eine Liste mit allen Pendenzen zurueck. Sollte keine Pendenze gefunden werden oder ein Fehler passieren, wird eine leere Liste zurueckgegeben.
	 * @return
     */
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxPendenzJA> getAllPendenzenJA() {
		//todo team Wenn das Feld Status in AbstractAntragEntity implementiert wird, muessen wir hier nur die Antraege zurueckgeben, die noch nicht bearbeitet wurden
		Collection<Gesuch> gesucheList = gesuchService.getAllGesuche();

		List<JaxPendenzJA> pendenzenList = new ArrayList<>();
		for (Gesuch gesuch : gesucheList) {
			if (gesuch.getFall() != null) {
				JaxPendenzJA pendenz = new JaxPendenzJA();
				pendenz.setAntragId(gesuch.getId());
				pendenz.setFallNummer(gesuch.getFall().getFallNummer());
				pendenz.setFamilienName(gesuch.getGesuchsteller1() != null ? gesuch.getGesuchsteller1().getNachname() : "");
				pendenz.setEingangsdatum(gesuch.getEingangsdatum());
				pendenz.setAngebote(createAngeboteList(gesuch.getKindContainers()));
				pendenz.setAntragTyp(AntragTyp.GESUCH); // todo team fuer Mutationen musst dieser wert AntragTyp.MUTATION sein
				pendenz.setInstitutionen(createInstitutionenList(gesuch.getKindContainers()));
				pendenz.setGesuchsperiode(converter.gesuchsperiodeToJAX(gesuch.getGesuchsperiode()));
				if (gesuch.getFall().getVerantwortlicher() != null) {
					pendenz.setVerantwortlicher(gesuch.getFall().getVerantwortlicher().getFullName());
				}

				pendenzenList.add(pendenz);
			}
		}
		return pendenzenList;
	}

	/**
	 * Geht durch die ganze Liste von KindContainers durch und gibt ein Set mit den Namen aller Institutionen zurueck.
	 * Da ein Set zurueckgegeben wird, sind die Daten nie dupliziert.
	 * @param kindContainers
	 * @return
     */
	private Set<String> createInstitutionenList(Set<KindContainer> kindContainers) {
		Set<String> resultSet = new HashSet<>();
		kindContainers.forEach(kindContainer -> {
			kindContainer.getBetreuungen().forEach(betreuung -> {
				if (betreuung.getInstitutionStammdaten() != null && betreuung.getInstitutionStammdaten().getInstitution() != null) {
					resultSet.add(betreuung.getInstitutionStammdaten().getInstitution().getName());
				}
			});
		});
		return resultSet;
	}

	/**
	 * Geht durch die ganze Liste von KindContainers durch und gibt ein Set mit den BetreuungsangebotTyp aller Institutionen zurueck.
	 * Da ein Set zurueckgegeben wird, sind die Daten nie dupliziert.
	 * @param kindContainers
	 * @return
     */
	private Set<BetreuungsangebotTyp> createAngeboteList(Set<KindContainer> kindContainers) {
		Set<BetreuungsangebotTyp> resultSet = new HashSet<>();
		kindContainers.forEach(kindContainer -> {
			kindContainer.getBetreuungen().forEach(betreuung -> {
				resultSet.add(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());
			});
		});
		return resultSet;
	}
}
