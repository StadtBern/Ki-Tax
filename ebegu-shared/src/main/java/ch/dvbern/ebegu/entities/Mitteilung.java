package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import ch.dvbern.ebegu.util.EbeguUtil;
import ch.dvbern.ebegu.validators.CheckMitteilungCompleteness;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_TEXTAREA_LENGTH;

/**
 * Entitaet zum Speichern von Mitteilungen in der Datenbank.
 */
@Audited
@Entity
@CheckMitteilungCompleteness
public class Mitteilung extends AbstractEntity {

	private static final long serialVersionUID = 489324250198016526L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_mitteilung_fall_id"))
	private Fall fall;

	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_mitteilung_betreuung_id"))
	private Betreuung betreuung;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MitteilungTeilnehmerTyp senderTyp;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MitteilungTeilnehmerTyp empfaengerTyp;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Mitteilung_sender"))
	private Benutzer sender;

	@Nullable
	@ManyToOne(optional = true)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_Mitteilung_empfaenger"))
	private Benutzer empfaenger;

	@Size(min = 0, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String subject;

	@Size(min = 0, max = DB_TEXTAREA_LENGTH)
	@Column(nullable = true)
	@Nullable
	private String message;

	@NotNull
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MitteilungStatus mitteilungStatus;

	@Nullable
	@Column(nullable = true)
	private LocalDateTime sentDatum;


	@NotNull
	public Fall getFall() {
		return fall;
	}

	public void setFall(@NotNull Fall fall) {
		this.fall = fall;
	}

	public Betreuung getBetreuung() {
		return betreuung;
	}

	public void setBetreuung(Betreuung betreuung) {
		this.betreuung = betreuung;
	}

	public MitteilungTeilnehmerTyp getSenderTyp() {
		return senderTyp;
	}

	public void setSenderTyp(MitteilungTeilnehmerTyp senderTyp) {
		this.senderTyp = senderTyp;
	}

	public MitteilungTeilnehmerTyp getEmpfaengerTyp() {
		return empfaengerTyp;
	}

	public void setEmpfaengerTyp(MitteilungTeilnehmerTyp empfaengerTyp) {
		this.empfaengerTyp = empfaengerTyp;
	}

	public Benutzer getSender() {
		return sender;
	}

	public void setSender(Benutzer sender) {
		this.sender = sender;
	}

	@Nullable
	public Benutzer getEmpfaenger() {
		return empfaenger;
	}

	public void setEmpfaenger(@Nullable Benutzer empfaenger) {
		this.empfaenger = empfaenger;
	}

	@Nullable
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Nullable
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MitteilungStatus getMitteilungStatus() {
		return mitteilungStatus;
	}

	public void setMitteilungStatus(MitteilungStatus mitteilungStatus) {
		this.mitteilungStatus = mitteilungStatus;
	}

	@Nullable
	public LocalDateTime getSentDatum() {
		return sentDatum;
	}

	public void setSentDatum(@Nullable LocalDateTime sentDatum) {
		this.sentDatum = sentDatum;
	}

	public boolean isEntwurf() {
		return MitteilungStatus.ENTWURF.equals(this.mitteilungStatus);
	}

	@SuppressWarnings({"OverlyComplexBooleanExpression", "OverlyComplexMethod"})
	@Override
	public boolean isSame(AbstractEntity other) {
		//noinspection ObjectEquality
		if (this == other) {
			return true;
		}
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		final Mitteilung otherMitteilung = (Mitteilung) other;
		return EbeguUtil.isSameObject(getBetreuung(), otherMitteilung.getBetreuung()) &&
			Objects.equals(getSender().getId(), otherMitteilung.getSender().getId()) &&
			Objects.equals(getSenderTyp(), otherMitteilung.getSenderTyp()) &&
			Objects.equals(getSentDatum(), otherMitteilung.getSentDatum()) &&
			EbeguUtil.isSameObject(getEmpfaenger(), otherMitteilung.getEmpfaenger()) &&
			Objects.equals(getEmpfaengerTyp(), otherMitteilung.getEmpfaengerTyp()) &&
			Objects.equals(getSubject(), otherMitteilung.getSubject()) &&
			Objects.equals(getMessage(), otherMitteilung.getMessage()) &&
			getMitteilungStatus() == otherMitteilung.getMitteilungStatus();
	}
}
