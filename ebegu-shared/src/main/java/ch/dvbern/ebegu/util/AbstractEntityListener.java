package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.SequenceType;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.KindService;
import ch.dvbern.ebegu.services.MandantService;
import ch.dvbern.ebegu.services.SequenceService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Optional;

public class AbstractEntityListener {

	private static PrincipalBean principalBean = null;
	private static MandantService mandantService = null;
	private FallService fallService;
	private KindService kindService;
	private SequenceService sequenceService;

	@SuppressFBWarnings(value = "LI_LAZY_INIT_STATIC", justification = "Auch wenn das vlt. mehrfach initialisiert wird... das macht nix, solange am Ende was Richtiges drinsteht")
	private static PrincipalBean getPrincipalBean() {
		if (principalBean == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection (mal wieder) buggy ist.
			//noinspection NonThreadSafeLazyInitialization
			principalBean = CDI.current().select(PrincipalBean.class).get();
		}
		return principalBean;
	}

	@SuppressFBWarnings(value = "LI_LAZY_INIT_STATIC", justification = "Auch wenn das vlt. mehrfach initialisiert wird... das macht nix, solange am Ende was Richtiges drinsteht")
	private static MandantService getMandantService() {
		if (mandantService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection (mal wieder) buggy ist.
			//noinspection NonThreadSafeLazyInitialization
			mandantService = CDI.current().select(MandantService.class).get();
		}
		return mandantService;
	}

	@PrePersist
	protected void prePersist(@Nonnull AbstractEntity entity) {
		LocalDateTime now = LocalDateTime.now();
		entity.setTimestampErstellt(now);
		entity.setTimestampMutiert(now);
		entity.setUserErstellt(getPrincipalBean().getPrincipal().getName());
		entity.setUserMutiert(getPrincipalBean().getPrincipal().getName());
		if (entity instanceof KindContainer) {
			KindContainer kind = (KindContainer) entity;
			Optional<Fall> optFall = getFallService().findFall(kind.getGesuch().getFall().getId());
			if (optFall.isPresent()) {
				Fall fall = optFall.get();
				kind.setKindNummer(fall.getNextNumberKind());
				fall.setNextNumberKind(fall.getNextNumberKind() + 1);
			}
		}
		else if (entity instanceof Betreuung) {
			Betreuung betreuung = (Betreuung) entity;
			Optional<KindContainer> optKind = getKindService().findKind(betreuung.getKind().getId());
			if (optKind.isPresent()) {
				KindContainer kindContainer = optKind.get();
				betreuung.setBetreuungNummer(kindContainer.getNextNumberBetreuung());
				kindContainer.setNextNumberBetreuung(kindContainer.getNextNumberBetreuung() + 1);
			}
		}
		else if (entity instanceof Fall) {
			Fall fall = (Fall) entity;
			Mandant mandant = getMandantService().getFirst(); //todo team der mandant sollte aus dem prinipal gelesen werden
			Long nextFallNr = getSequenceService().createNumberTransactional(SequenceType.FALL_NUMMER, mandant);
			fall.setFallNummer(nextFallNr);
		}
	}

	@PreUpdate
	public void preUpdate(@Nonnull AbstractEntity entity) {
		entity.setTimestampMutiert(LocalDateTime.now());
		entity.setUserMutiert(getPrincipalBean().getPrincipal().getName());

	}

	private FallService getFallService() {
		if (fallService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection in Wildfly 10 nicht funktioniert.
			//noinspection NonThreadSafeLazyInitialization
			fallService = CDI.current().select(FallService.class).get();
		}
		return fallService;
	}

	private KindService getKindService() {
		if (kindService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection in Wildfly 10 nicht funktioniert.
			//noinspection NonThreadSafeLazyInitialization
			kindService = CDI.current().select(KindService.class).get();
		}
		return kindService;
	}

	private SequenceService getSequenceService() {
		if (sequenceService == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection in Wildfly 10 nicht funktioniert.
			//noinspection NonThreadSafeLazyInitialization
			sequenceService = CDI.current().select(SequenceService.class).get();
		}
		return sequenceService;
	}
}
