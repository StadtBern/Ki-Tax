package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.enums.UserRoleName;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Interface um gewisse Services als SUPER_ADMIN aufrufen zu koennen
 */
@Stateless
@Local(SuperAdminService.class)
@RunAs(value = UserRoleName.SUPER_ADMIN)
public class SuperAdminServiceBean implements SuperAdminService {

	@Inject
	private GesuchService gesuchService;

	@Override
	@RolesAllowed(value = {SUPER_ADMIN, ADMIN})
	public void removeGesuch(@Nonnull String gesuchId) {
		gesuchService.removeGesuch(gesuchId);
	}

}
