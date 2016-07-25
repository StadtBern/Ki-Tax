package ch.dvbern.ebegu.entities;

import ch.dvbern.ebegu.enums.DokumentGrundTyp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DokumentGrund.class)
public abstract class DokumentGrund_ extends ch.dvbern.ebegu.entities.AbstractEntity_ {

	public static volatile SingularAttribute<DokumentGrund, DokumentGrundTyp> dokumentGrundTyp;
	public static volatile SingularAttribute<DokumentGrund, String> fullName;
	public static volatile SingularAttribute<DokumentGrund, String> tag;
	public static volatile SingularAttribute<DokumentGrund, Gesuch> gesuch;
	public static volatile SetAttribute<DokumentGrund, Dokument> dokumente;

}

