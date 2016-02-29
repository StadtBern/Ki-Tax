package ch.dvbern.ebegu.util;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.AbstractEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class AbstractEntityListener {

//		@Inject
//		private PrincipalBean principalBean;

	private static PrincipalBean principalBean = null;

	@SuppressFBWarnings(value = "LI_LAZY_INIT_STATIC", justification = "Auch wenn das vlt. mehrfach initialisiert wird... das macht nix, solange am Ende was Richtiges drinsteht")
	private static PrincipalBean getPrincipalBean() {
		if (principalBean == null) {
			//FIXME: das ist nur ein Ugly Workaround, weil CDI-Injection (mal wieder) buggy ist.
			//noinspection NonThreadSafeLazyInitialization
			principalBean = CDI.current().select(PrincipalBean.class).get();
		}
		return principalBean;
	}


//	public PrincipalBean getPrincipalBean() {
//		return principalBean;
//	}

	@PrePersist
	protected void prePersist(@Nonnull AbstractEntity entity) {
		LocalDateTime now = LocalDateTime.now();
		entity.setTimestampErstellt(now);
		entity.setTimestampMutiert(now);
		entity.setUserErstellt(getPrincipalBean().getPrincipal().getName());
		entity.setUserMutiert(getPrincipalBean().getPrincipal().getName());
	}

	@PreUpdate
	public void preUpdate(@Nonnull AbstractEntity entity) {
		entity.setTimestampMutiert(LocalDateTime.now());
		entity.setUserMutiert(getPrincipalBean().getPrincipal().getName());

	}

}
