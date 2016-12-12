package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.types.DateRange;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Container-Entity für die GesuchstellerAdressen
 */
@Audited
@Entity
public class GesuchstellerAdresseContainer extends AbstractEntity {


	private static final long serialVersionUID = -3084333639027795652L;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellerContainer_id"))
	private GesuchstellerContainer gesuchstellerContainer;

	@Valid
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellergs_id"))
	private GesuchstellerAdresse gesuchstellerAdresseGS;

	@Valid
	@NotNull
	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_gesuchstelleradresse_container_gesuchstellerja_id"))
	private GesuchstellerAdresse gesuchstellerAdresseJA;



	public GesuchstellerAdresseContainer() {}

	public GesuchstellerContainer getGesuchstellerContainer() {
		return gesuchstellerContainer;
	}

	public void setGesuchstellerContainer(GesuchstellerContainer gesuchstellerContainer) {
		this.gesuchstellerContainer = gesuchstellerContainer;
	}

	public GesuchstellerAdresse getGesuchstellerAdresseGS() {
		return gesuchstellerAdresseGS;
	}

	public void setGesuchstellerAdresseGS(GesuchstellerAdresse gesuchstellerAdresseGS) {
		this.gesuchstellerAdresseGS = gesuchstellerAdresseGS;
	}

	public GesuchstellerAdresse getGesuchstellerAdresseJA() {
		return gesuchstellerAdresseJA;
	}

	public void setGesuchstellerAdresseJA(GesuchstellerAdresse gesuchstellerAdresseJA) {
		this.gesuchstellerAdresseJA = gesuchstellerAdresseJA;
	}

	/**
	 * Fragt nach dem Wert der AdresseJA, welcher eigentlich der geltende Wert ist
	 */
	@Transient
	public boolean extractIsKorrespondenzAdresse() {
		return this.gesuchstellerAdresseJA != null && this.gesuchstellerAdresseJA.isKorrespondenzAdresse();
	}

	/**
	 * Extracts the value of nichtInGemeinde von gesuchstellerAdresseJA
	 */
	@Transient
	public boolean extractIsNichtInGemeinde() {
		return this.gesuchstellerAdresseJA != null && this.gesuchstellerAdresseJA.isNichtInGemeinde();
	}

	/**
	 * Extracts the Gueltigkeit von gesuchstellerAdresseJA
	 */
	public DateRange extractGueltigkeit() {
		return this.gesuchstellerAdresseJA.getGueltigkeit();
	}

	/**
	 * Extracts the AdresseTyp von gesuchstellerAdresseJA
	 */
	public AdresseTyp extractAdresseTyp() {
		return this.gesuchstellerAdresseJA.getAdresseTyp();
	}

	/**
	 * Extracts the Hausnummer von gesuchstellerAdresseJA
	 */
	public String extractHausnummer() {
		return this.gesuchstellerAdresseJA.getHausnummer();
	}

	/**
	 * Extracts the Strasse von gesuchstellerAdresseJA
	 */
	public String extractStrasse() {
		return this.gesuchstellerAdresseJA.getStrasse();
	}

	/**
	 * Extracts the Zusatzzeile von gesuchstellerAdresseJA
	 */
	public String extractZusatzzeile() {
		return this.gesuchstellerAdresseJA.getZusatzzeile();
	}

	/**
	 * Extracts the PLZ von gesuchstellerAdresseJA
	 */
	public String extractPlz() {
		return this.gesuchstellerAdresseJA.getPlz();
	}

	/**
	 * Extracts the Ort von gesuchstellerAdresseJA
	 */
	public String extractOrt() {
		return this.gesuchstellerAdresseJA.getOrt();
	}

	/**
	 * Extracts the Organisation von gesuchstellerAdresseJA
	 */
	public String extractOrganisation() {
		return this.gesuchstellerAdresseJA.getOrganisation();
	}

	public GesuchstellerAdresseContainer copyForMutation(GesuchstellerAdresseContainer mutation, GesuchstellerContainer gesuchstellerContainer) {
		super.copyForMutation(mutation);
		mutation.setGesuchstellerContainer(gesuchstellerContainer);
		mutation.setGesuchstellerAdresseGS(null);
		if (this.getGesuchstellerAdresseJA() != null) {
			mutation.setGesuchstellerAdresseJA(this.getGesuchstellerAdresseJA().copyForMutation(new GesuchstellerAdresse()));
		}
		return mutation;
	}
}
