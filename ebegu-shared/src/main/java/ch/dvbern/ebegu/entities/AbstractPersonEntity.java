package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.dto.suchfilter.lucene.EbeguLocalDateBridge;
import ch.dvbern.ebegu.enums.Geschlecht;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static ch.dvbern.ebegu.util.Constants.DB_DEFAULT_MAX_LENGTH;

/**
 * Abstract Entity for "Gesuchsteller-like" Entities.
 */
@MappedSuperclass
@Audited
public abstract class AbstractPersonEntity extends AbstractEntity {

	private static final long serialVersionUID = -9037857320548372570L;

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	@NotNull
	private Geschlecht geschlecht;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@Column(nullable = false)
	@NotNull
	@Field()
	private String vorname;

	@Size(min = 1, max = DB_DEFAULT_MAX_LENGTH)
	@NotNull
	@Column(nullable = false)
	@Field()
	private String nachname;

	@Column(nullable = false)
	@NotNull
	@FieldBridge(impl=EbeguLocalDateBridge.class)   //wir indizieren dates als string
	@Field(analyze= Analyze.NO) //datumsfelder nicht tokenizen etc
	private LocalDate geburtsdatum;


	public AbstractPersonEntity() {
	}


	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public Geschlecht getGeschlecht() {
		return geschlecht;
	}

	public void setGeschlecht(Geschlecht geschlecht) {
		this.geschlecht = geschlecht;
	}

	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}

	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}

	public String getFullName() {
		return vorname + " " + nachname;
	}

	public AbstractPersonEntity copyForMutation(AbstractPersonEntity mutation) {
		super.copyForMutation(mutation);
		mutation.setGeschlecht(this.getGeschlecht());
		mutation.setVorname(this.getVorname());
		mutation.setNachname(this.getNachname());
		mutation.setGeburtsdatum(this.getGeburtsdatum());
		return mutation;
	}
}
