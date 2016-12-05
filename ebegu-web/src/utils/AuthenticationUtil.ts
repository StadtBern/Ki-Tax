import TSUser from '../models/TSUser';
import {IStateService} from 'angular-ui-router';
import {TSRoleUtil} from './TSRoleUtil';
export default class AuthenticationUtil {


    /**
     *  Navigiert basierend auf der Rolle zu einer anderen Startseite
     */
    public static navigateToStartPageForRole(user: TSUser, $state: IStateService): void {
        if (TSRoleUtil.getAdministratorJugendamtRole().indexOf(user.role) > -1) {
            $state.go('pendenzen');
        } else if (TSRoleUtil.getTraegerschaftInstitutionOnlyRoles().indexOf(user.role) > -1) {
            $state.go('pendenzenInstitution');
        } else if (TSRoleUtil.getSchulamtOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else if (TSRoleUtil.getGesuchstellerOnlyRoles().indexOf(user.role) > -1) {
            $state.go('gesuchstellerDashboard');
        } else {
            console.error('Achtung, keine Startpage definiert fuer Rolle ', user.getRoleKey(), ', nehme gesuchstellerDashboard');
            $state.go('gesuchstellerDashboard');
        }
    }
}
