package net.bull.javamelody;
import javax.persistence.EntityManager;

/**
 * Dient nur dazu, die Methode JpaWrapper.createEntityManagerProxy aufzurufen, da sie leider package-private ist :(
 */
public final class JpaWrapperPublic {
	private JpaWrapperPublic() {
		// utility class
	}

	static {
		Counter jpaCounter = MonitoringProxy.getJpaCounter();
		jpaCounter.setUsed(true);
		jpaCounter.setDisplayed(true);
	}

	public static EntityManager wrapForMonitoring(EntityManager em) {
		return JpaWrapper.createEntityManagerProxy(em);
	}
}
