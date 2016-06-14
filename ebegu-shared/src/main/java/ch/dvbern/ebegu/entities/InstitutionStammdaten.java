package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


/**
 * Entitaet zum Speichern von InstitutionStammdaten in der Datenbank.
 */
@Audited
@Entity
public class InstitutionStammdaten extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -8403411439882700618L;

	@Column(nullable = true)
	private IBAN iban;

	@DecimalMin("0.00")
	@DecimalMax("365.00")
	@Nullable
	private BigDecimal oeffnungstage;

	@DecimalMin("0.00")
	@DecimalMax("24.00")
	@Nullable
	private BigDecimal oeffnungsstunden;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	private BetreuungsangebotTyp betreuungsangebotTyp;

	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_institution_stammdaten_institution_id"), nullable = false)
	private Institution institution;

	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_institutionStammdaten_adresse_id"), nullable = false)
	private Adresse adresse;

	public InstitutionStammdaten() {
	}

	public IBAN getIban() {
		return iban;
	}

	public void setIban(IBAN iban) {
		this.iban = iban;
	}

	public BigDecimal getOeffnungstage() {
		return oeffnungstage;
	}

	public void setOeffnungstage(BigDecimal oeffnungstage) {
		this.oeffnungstage = oeffnungstage;
	}

	public BigDecimal getOeffnungsstunden() {
		return oeffnungsstunden;
	}

	public void setOeffnungsstunden(BigDecimal oeffnungsstunden) {
		this.oeffnungsstunden = oeffnungsstunden;
	}

	@Nullable
	public BetreuungsangebotTyp getBetreuungsangebotTyp() {
		return betreuungsangebotTyp;
	}

	public void setBetreuungsangebotTyp(@Nullable BetreuungsangebotTyp betreuungsangebotTyp) {
		this.betreuungsangebotTyp = betreuungsangebotTyp;
	}

	public Institution getInstitution() {
		return institution;
	}

	public void setInstitution(Institution institution) {
		this.institution = institution;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
}
