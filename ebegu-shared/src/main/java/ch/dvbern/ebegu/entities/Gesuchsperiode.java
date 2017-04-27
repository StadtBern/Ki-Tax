package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.types.DateRange;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entity fuer Gesuchsperiode.
 */
@Audited
@Entity
public class Gesuchsperiode extends AbstractDateRangedEntity {

	private static final long serialVersionUID = -9132257370971574570L;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GesuchsperiodeStatus status = GesuchsperiodeStatus.ENTWURF;


	public GesuchsperiodeStatus getStatus() {
		return status;
	}

	public void setStatus(GesuchsperiodeStatus status) {
		this.status = status;
	}

	public int getBasisJahr() {
		return getGueltigkeit().getGueltigAb().getYear() - 1;
	}

	public int getBasisJahrPlus1() {
		return getBasisJahr() + 1;
	}

	public int getBasisJahrPlus2() {
		return getBasisJahr() + 2;
	}

	@SuppressWarnings({"ObjectEquality", "OverlyComplexBooleanExpression"})
	public boolean isSame(Gesuchsperiode otherGesuchsperiode) {
		boolean dateRangeIsSame = super.isSame(otherGesuchsperiode);
		boolean statusSame = Objects.equals(this.getStatus(), otherGesuchsperiode.getStatus());
		return dateRangeIsSame && statusSame;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("gueltigkeit", getGueltigkeit().toString())
			.append("status", status.name())
			.toString();
	}

	public String getGesuchsperiodeString() {
		DateRange gueltigkeit = this.getGueltigkeit();
		return gueltigkeit.getGueltigAb().getYear() + "/"
			+ gueltigkeit.getGueltigBis().getYear();
	}
}
