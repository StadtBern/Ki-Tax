package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.converters.LocalDateTimePersistenceConverter;
import ch.dvbern.ebegu.util.AbstractEntityListener;
import ch.dvbern.ebegu.util.Constants;
import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.hibernate.Hibernate;

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
@EntityListeners(AbstractEntityListener.class)
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
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime timestampErstellt;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	@Column(nullable = false)
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime timestampMutiert;

	// Wert darf nicht leer sein, aber kein @NotNull, da Wert erst im @PrePersist gesetzt
	// wir verwenden hier die Hibernate spezifische Annotation, weil diese vererbt wird
	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userErstellt;

	@Size(max = Constants.UUID_LENGTH)
	@Column(nullable = false, length = Constants.UUID_LENGTH)
	private String userMutiert;

	public AbstractEntity() {
		//da wir teilweise schon eine id brauchen bevor die Entities gespeichert werden initialisieren wir die uuid hier
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}

	@Nullable // Nullable, da erst im PrePersist gesetzt
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
	 * @return true wenn das entity noch nicht in der DB gespeichert wurde (i.e. keine ID gesetzt hat)
	 */
	public boolean isNew() {
		return timestampErstellt == null;
	}

	/** //todo team probably delete this
	 * Hilfsmethode fuer toString(): Wenn beim Debugging eine JPA-Referenz schon detached ist,
	 * kann nicht mehr auf den Wert zugegriffen werden und es kommt eine Exception.
	 * Diese Methode faengt die Exception ab und gibt einen fixen Text zurueck.
	 * <pre>
	 * {@code
	 *	public String toString() {
	 *		return MoreObjects.toStringHelper(this)
	 *			.add("id", getId())
	 *			.add("kontaktperson", getSilent(() -> kontaktperson))
	 *			.add("kind", getSilent(() -> kind))
	 *			.toString();
	 *	}
	 * }
	 * </pre>
	 */
/*	protected <T extends Serializable> String getSilent(Supplier<T> supplier) {
		try {
			return String.valueOf(supplier.get());
		} catch (RuntimeException ignored) {
			return "<unknown>";
		}
	}*/

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", getId()).toString();
	}
}
