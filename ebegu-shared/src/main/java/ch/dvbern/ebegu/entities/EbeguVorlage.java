package ch.dvbern.ebegu.entities;


import ch.dvbern.ebegu.enums.EbeguVorlageKey;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import org.hibernate.envers.Audited;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Entitaet zum Speichern von zeitabhängigen Vorlagen in E-BEGU
 */
@Audited
@Entity
public class EbeguVorlage extends AbstractDateRangedEntity implements Comparable<EbeguVorlage> {


	private static final long serialVersionUID = 8704632842261673111L;

	@NotNull
	@Column(nullable = false, length = Constants.DB_DEFAULT_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private EbeguVorlageKey name;

	@Nullable
	@OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_ebeguvorlage_vorlage_id"), nullable = true)
	private Vorlage vorlage;

	@Column(nullable = false)
	@Nullable
	private boolean proGesuchsperiode = true;

	public EbeguVorlage() {
	}

	public EbeguVorlage(EbeguVorlageKey name) {
		this(name, Constants.DEFAULT_GUELTIGKEIT, true);
	}

	public EbeguVorlage(EbeguVorlageKey name, DateRange gueltigkeit) {
		this(name, gueltigkeit, true);
	}

	public EbeguVorlage(EbeguVorlageKey name, DateRange gueltigkeit, boolean proGesuchsperiode) {
		this.name = name;
		this.proGesuchsperiode = proGesuchsperiode;
		this.setGueltigkeit(gueltigkeit);
	}

	@Nonnull
	public EbeguVorlageKey getName() {
		return name;
	}

	public void setName(@Nonnull EbeguVorlageKey name) {
		this.name = name;
	}

	public Vorlage getVorlage() {
		return vorlage;
	}

	public void setVorlage(Vorlage vorlage) {
		this.vorlage = vorlage;
	}

	@Nullable
	public boolean isProGesuchsperiode() {
		return proGesuchsperiode;
	}

	public void setProGesuchsperiode(@Nullable boolean proGesuchsperiode) {
		this.proGesuchsperiode = proGesuchsperiode;
	}

	/**
	 * @param gueltigkeit
	 * @return a copy of the current Param with the gueltigkeit set to the passed DateRange
	 */
	public EbeguVorlage copy(DateRange gueltigkeit) {
		EbeguVorlage copiedParam = new EbeguVorlage();
		copiedParam.setGueltigkeit(new DateRange(gueltigkeit.getGueltigAb(), gueltigkeit.getGueltigBis()));
		copiedParam.setName(this.getName());
		copiedParam.setProGesuchsperiode(this.isProGesuchsperiode());
		return copiedParam;
	}

	@Override
	public int compareTo(EbeguVorlage o) {
		return this.getName().compareTo(o.getName());
	}
}
