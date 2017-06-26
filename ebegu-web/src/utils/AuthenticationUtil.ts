import TSUser from '../models/TSUser';
import {IStateService} from 'angular-ui-router';
import {TSRoleUtil} from './TSRoleUtil';
import {TSRole} from '../models/enums/TSRole';
export default class AuthenticationUtil {


    /**
     *  Navigiert basierend auf der Rolle zu einer anderen Startseite
     */
    public static navigateToStartPageForRole(user: TSUser, $state: IStateService): void {
        if (user.role === TSRole.SUPER_ADMIN) {
            $state.go('faelle');
        } else if (TSRoleUtil.getAdministratorJugendamtRole().indexOf(user.role) > -1) {
            $state.go('pendenzen');
        } else if (TSRoleUtil.getTraegerschaftInstitutionOnlyRoles().indexOf(user.role) > -1) {
            $state.go('pendenzenInstitution');
        } else if (TSRoleUtil.getSchulamtOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else if (TSRoleUtil.getSteueramtOnlyRoles().indexOf(user.role) > -1) {
            $state.go('pendenzenSteueramt');
        } else if (TSRoleUtil.getGesuchstellerOnlyRoles().indexOf(user.role) > -1) {
            $state.go('gesuchstellerDashboard');
        } else if (TSRoleUtil.getJuristOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else if (TSRoleUtil.getRevisorOnlyRoles().indexOf(user.role) > -1) {
            $state.go('faelle');
        } else {
            console.error('Achtung, keine Startpage definiert fuer Rolle ', user.getRoleKey(), ', nehme gesuchstellerDashboard');
            $state.go('gesuchstellerDashboard');
        }
    }
}
