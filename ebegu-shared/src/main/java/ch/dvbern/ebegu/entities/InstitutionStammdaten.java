package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;


import static ch.dvbern.ebegu.util.Constants.DB_IBAN_LENGTH;

/**
 * Entitaet zum Speichern von InstitutionStammdaten in der Datenbank.
 */
@Audited
@Entity
public class InstitutionStammdaten extends AbstractEntity {

	private static final long serialVersionUID = -8403411439882700618L;

	@Size(min = 1, max = DB_IBAN_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String iban;

	@NotNull
	@DecimalMin("0.00")
	@DecimalMax("365.00")
	@Column(nullable = false)
	private BigDecimal oeffnungstage;

	@NotNull
	@DecimalMin("0.00")
	@DecimalMax("8760.00")
	@Column(nullable = false)
	private BigDecimal oeffnungsstunden;

	@Enumerated(value = EnumType.STRING)
	@Nullable
	private BetreuungsangebotTyp betreuungsangebotTyp;

	@NotNull
	@Column(nullable = false)
	private LocalDate datumVon;

	@NotNull
	@Column(nullable = false)
	private LocalDate datumBis;

	@ManyToOne(optional = false)
	private Institution institution;

}
