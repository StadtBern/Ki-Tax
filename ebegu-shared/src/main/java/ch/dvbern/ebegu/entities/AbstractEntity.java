package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.reporting.gesuchstichtag.GesuchStichtagDataRow;
import ch.dvbern.ebegu.reporting.gesuchzeitraum.GesuchZeitraumDataRow;
import ch.dvbern.ebegu.util.AbstractEntityListener;
import ch.dvbern.ebegu.util.Constants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.Hibernate;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ClassReferencesSubclass")
@MappedSuperclass
@Audited
@EntityListeners(AbstractEntityListener.class)
//Mappings for the native quries used by the report
@SqlResultSetMappings({
	@SqlResultSetMapping(name = "GesuchStichtagDataRowMapping", classes = {
		@ConstructorResult(targetClass = GesuchStichtagDataRow.class,
			columns = {
				@ColumnResult(name = "bgNummer", type = String.class),
				@ColumnResult(name = "gesuchId", type = String.class),
				@ColumnResult(name = "institution", type = String.class),
				@ColumnResult(name = "betreuungsTyp", type = String.class),
				@ColumnResult(name = "periode", type = String.class),
				@ColumnResult(name = "nichtFreigegeben", type = Integer.class),
				@ColumnResult(name = "mahnungen", type = Integer.class),
				@ColumnResult(name = "beschwerde", type = Integer.class)}
		)}
	),
	@SqlResultSetMapping(name = "GesuchZeitraumDataRowMapping", classes = {
		@ConstructorResult(targetClass = GesuchZeitraumDataRow.class,
			columns = {
				@ColumnResult(name = "bgNummer", type = String.class),
				@ColumnResult(name = "gesuchId", type = String.class),
				@ColumnResult(name = "institution", type = String.class),
				@ColumnResult(name = "betreuungsTyp", type = String.class),
				@ColumnResult(name = "periode", type = String.class),
				@ColumnResult(name = "anzahlGesuchOnline", type = Integer.class),
				@ColumnResult(name = "anzahlGesuchPapier", type = Integer.class),
				@ColumnResult(name = "anzahlMutationOnline", type = Integer.class),
				@ColumnResult(name = "anzahlMutationPapier", type = Integer.class),
				@ColumnResult(name = "anzahlMutationAbwesenheit", type = Integer.class),
				@ColumnResult(name = "anzahlMutationBetreuung", type = Integer.class),
				@ColumnResult(name = "anzahlMutationDokumente", type = Integer.class),
				@ColumnResult(name = "anzahlMutationEV", type = Integer.class),
				@ColumnResult(name = "anzahlMutationEwerbspensum", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFamilienSitutation", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFinanzielleSituation", type = Integer.class),
				@ColumnResult(name = "anzahlMutationFreigabe", type = Integer.class),
				@ColumnResult(name = "anzahlMutationGesuchErstellen", type = Integer.class),
				@ColumnResult(name = "anzahlMutationGesuchsteller", type = Integer.class),
				@ColumnResult(name = "anzahlMutationKinder", type = Integer.class),
				@ColumnResult(name = "anzahlMutationUmzug", type = Integer.class),
				@ColumnResult(name = "anzahlMutationVerfuegen", type = Integer.class),
				@ColumnResult(name = "anzahlMahnungen", type = Integer.class),
				@ColumnResult(name = "anzahlBeschwerde", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungen", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenNormal", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenMaxEinkommen", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenKeinPensum", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenZuschlagZumPensum", type = Integer.class),
				@ColumnResult(name = "anzahlVerfuegungenNichtEintreten", type = Integer.class)}
		)}
	)
})
public abstract class AbstractEntity implements Serializable {

	private static final long serialVersionUID = -979317154050183445L;

	@Id
	@Column(unique = true, nullable = false, updatable = false, length = Constants.UUID_LENGTH)
	@Size(min = Constants.UUID_LENGTH, max = Constants.UUID_LENGTH)
	private String id;

	@Version
	@NotNull
	private long version;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	private LocalDateTime timestampErstellt;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	private LocalDateTime timestampMutiert;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	// wir verwenden hier die Hibernate spezifische Annotation, weil diese vererbt wird
	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userErstellt;

	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userMutiert;

	@Column(nullable = true, length = Constants.UUID_LENGTH)
	@Size(min = Constants.UUID_LENGTH, max = Constants.UUID_LENGTH)
	private String vorgaengerId;

	public AbstractEntity() {
		//da wir teilweise schon eine id brauchen bevor die Entities gespeichert werden initialisieren wir die uuid hier
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(@Nullable String id) {
		this.id = id;
	}

	// Nullable, da erst im PrePersist gesetzt
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public LocalDateTime getTimestampErstellt() {
		return timestampErstellt;
	}

	public void setTimestampErstellt(LocalDateTime timestampErstellt) {
		this.timestampErstellt = timestampErstellt;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public LocalDateTime getTimestampMutiert() {
		return timestampMutiert;
	}

	public void setTimestampMutiert(LocalDateTime timestampMutiert) {
		this.timestampMutiert = timestampMutiert;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public String getUserErstellt() {
		return userErstellt;
	}

	public void setUserErstellt(@Nonnull String userErstellt) {
		this.userErstellt = userErstellt;
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
	public String getUserMutiert() {
		return userMutiert;
	}

	public void setUserMutiert(@Nonnull String userMutiert) {
		this.userMutiert = userMutiert;
	}

	public String getVorgaengerId() {
		return vorgaengerId;
	}

	public void setVorgaengerId(String vorgaengerId) {
		this.vorgaengerId = vorgaengerId;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@SuppressFBWarnings(value = "BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS", justification = "Es wird Hibernate.getClass genutzt um von Proxies (LazyInit) die konkrete Klasse zu erhalten")
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
			return false;
		}

		AbstractEntity that = (AbstractEntity) o;

		Objects.requireNonNull(getId());
		Objects.requireNonNull(that.getId());

		return getId().equals(that.getId());
	}

	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	/**
	 * @return true wenn das entity noch nicht in der DB gespeichert wurde (i.e. keinen timestamp gesetzt hat)
	 */
	public boolean isNew() {
		return timestampErstellt == null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public boolean hasVorgaenger() {
		return vorgaengerId != null;
	}

	public AbstractEntity copyForMutation(AbstractEntity mutation) {
		mutation.setVorgaengerId(this.getId());
		return mutation;
	}
}
