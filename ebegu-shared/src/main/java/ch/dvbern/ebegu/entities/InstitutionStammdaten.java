package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entitaet zum Speichern von InstitutionStammdaten in der Datenbank.
 */
@Audited
@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(columnNames = "adresse_id", name = "UK_institution_stammdaten_adresse_id"),
	indexes = {
		@Index(name =  "IX_institution_stammdaten_gueltig_ab", columnList = "gueltigAb"),
		@Index(name =  "IX_institution_stammdaten_gueltig_bis", columnList = "gueltigBis")
	}
)
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class InstitutionStammdaten extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -8403411439882700618L;

	@Column(nullable = true)
	@Embedded
	@Valid
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
	@NotNull
	@Column(nullable = false)
	private BetreuungsangebotTyp betreuungsangebotTyp;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_institution_stammdaten_institution_id"), nullable = false)
	private Institution institution;

	@NotNull
	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_institution_stammdaten_adresse_id"), nullable = false)
	private Adresse adresse;

	public InstitutionStammdaten() {
	}

	public IBAN getIban() {
		return iban;
	}

	public void setIban(IBAN iban) {
		this.iban = iban;
	}

	@Nullable
	public BigDecimal getOeffnungstage() {
		return oeffnungstage;
	}

	public void setOeffnungstage(@Nullable BigDecimal oeffnungstage) {
		this.oeffnungstage = oeffnungstage;
	}

	@Nullable
	public BigDecimal getOeffnungsstunden() {
		return oeffnungsstunden;
	}

	public void setOeffnungsstunden(@Nullable BigDecimal oeffnungsstunden) {
		this.oeffnungsstunden = oeffnungsstunden;
	}

	@Nonnull
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

	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		if (!super.isSame(other)) {
			return false;
		}
		final InstitutionStammdaten otherInstStammdaten = (InstitutionStammdaten) other;
		return EbeguUtil.isSameObject(getInstitution(), otherInstStammdaten.getInstitution()) &&
			Objects.equals(getBetreuungsangebotTyp(), otherInstStammdaten.getBetreuungsangebotTyp()) &&
			Objects.equals(getIban(), otherInstStammdaten.getIban()) &&
			Objects.equals(getOeffnungsstunden(), otherInstStammdaten.getOeffnungsstunden()) &&
			Objects.equals(getOeffnungstage(), otherInstStammdaten.getOeffnungstage()) &&
			EbeguUtil.isSameObject(getAdresse(), otherInstStammdaten.getAdresse());
	}
}
