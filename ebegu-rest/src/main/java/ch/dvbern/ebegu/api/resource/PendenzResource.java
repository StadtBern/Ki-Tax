package ch.dvbern.ebegu.api.resource;

import ch.dvbern.ebegu.api.converter.AntragStatusConverter;
import ch.dvbern.ebegu.api.converter.JaxBConverter;
import ch.dvbern.ebegu.api.dtos.JaxPendenzInstitution;
import ch.dvbern.ebegu.api.dtos.JaxPendenzJA;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.services.BetreuungService;
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

	@Inject
	private BetreuungService betreuungService;
	@Inject
	private AntragStatusConverter antragStatusConverter;

	/**
	 * Gibt eine Liste mit allen Pendenzen des Jugendamtes zurueck. Sollte keine Pendenze gefunden werden oder ein Fehler passieren, wird eine leere Liste zurueckgegeben.
     */
	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public List<JaxPendenzJA> getAllPendenzenJA() {
		Collection<Gesuch> gesucheList = gesuchService.getAllActiveGesuche();

		List<JaxPendenzJA> pendenzenList = new ArrayList<>();
		// todo team fuer Mutationen musst dieser wert AntragTyp.MUTATION sein
		gesucheList.stream().filter(gesuch -> gesuch.getFall() != null).forEach(gesuch -> {
			JaxPendenzJA pendenz = new JaxPendenzJA();
			pendenz.setAntragId(gesuch.getId());
			pendenz.setFallNummer(gesuch.getFall().getFallNummer());
			pendenz.setFamilienName(gesuch.getGesuchsteller1() != null ? gesuch.getGesuchsteller1().getNachname() : "");
			pendenz.setEingangsdatum(gesuch.getEingangsdatum());
			pendenz.setAngebote(createAngeboteList(gesuch.getKindContainers()));
			pendenz.setAntragTyp(AntragTyp.GESUCH); // todo team fuer Mutationen musst dieser wert AntragTyp.MUTATION sein
			pendenz.setStatus(antragStatusConverter.convertStatusToDTO(gesuch, gesuch.getStatus()));
			pendenz.setInstitutionen(createInstitutionenList(gesuch.getKindContainers()));
			pendenz.setGesuchsperiode(converter.gesuchsperiodeToJAX(gesuch.getGesuchsperiode()));
			if (gesuch.getFall().getVerantwortlicher() != null) {
				pendenz.setVerantwortlicher(gesuch.getFall().getVerantwortlicher().getFullName());
			}

			pendenzenList.add(pendenz);
		});
		return pendenzenList;
	}

	@Nonnull
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/institution")
	public List<JaxPendenzInstitution> getAllPendenzenInstitution() {
		Collection<Betreuung> betreuungenInStatus = betreuungService.getPendenzenForInstitutionsOrTraegerschaftUser();
		List<JaxPendenzInstitution> pendenzenList = new ArrayList<>();
		for (Betreuung betreuung : betreuungenInStatus) {
			JaxPendenzInstitution pendenz = new JaxPendenzInstitution();
			pendenz.setBetreuungsNummer(betreuung.getBGNummer());
			pendenz.setBetreuungsId(betreuung.getId());
			pendenz.setGesuchId(betreuung.extractGesuch().getId());
			pendenz.setKindId(betreuung.getKind().getId());
			pendenz.setName(betreuung.getKind().getKindJA().getNachname());
			pendenz.setVorname(betreuung.getKind().getKindJA().getVorname());
			pendenz.setGeburtsdatum(betreuung.getKind().getKindJA().getGeburtsdatum());
			pendenz.setEingangsdatum(betreuung.extractGesuch().getEingangsdatum());
			pendenz.setGesuchsperiode(converter.gesuchsperiodeToJAX(betreuung.extractGesuchsperiode()));
			pendenz.setBetreuungsangebotTyp(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp());
			pendenz.setInstitution(converter.institutionToJAX(betreuung.getInstitutionStammdaten().getInstitution()));
			pendenz.setTyp("PLATZBESTAETIGUNG"); //TODO (Team) Wenn wir dann die Mutationstypen haben, muss dies angepasst werden!
			pendenzenList.add(pendenz);
		}
		return pendenzenList;
	}

	/**
	 * Geht durch die ganze Liste von KindContainers durch und gibt ein Set mit den Namen aller Institutionen zurueck.
	 * Da ein Set zurueckgegeben wird, sind die Daten nie dupliziert.
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
