package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.MitteilungStatus;
import ch.dvbern.ebegu.enums.MitteilungTeilnehmerTyp;
import org.hibernate.envers.Audited;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;
import static ch.dvbern.ebegu.util.Constants.DB_TEXTAREA_LENGTH;

/**
 * Entitaet zum Speichern von Mitteilungen in der Datenbank.
 */
@Audited
@Entity
public class Mitteilung extends AbstractEntity {

	private static final long serialVersionUID = 489324250198016526L;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_mitteilung_fall_id"))
	private Fall fall;

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

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	private String subject;

	@Size(min = 1, max = DB_TEXTAREA_LENGTH)
	@Column(nullable = false)
	@NotNull
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

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
}
