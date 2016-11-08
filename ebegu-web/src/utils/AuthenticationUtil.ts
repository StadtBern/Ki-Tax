import TSUser from '../models/TSUser';
import {IStateService} from 'angular-ui-router';
export default class AuthenticationUtil {


    /**
     *  Navigiert basierend auf der Rolle zu einer anderen Startseite
     */
    public static navigateToStartPageForRole(user: TSUser, $state: IStateService): void {
        if (user.getRoleKey() === 'TSRole_SACHBEARBEITER_JA' || user.getRoleKey() === 'TSRole_ADMIN') {
            $state.go('pendenzen');
        } else if (user.getRoleKey() === 'TSRole_SACHBEARBEITER_INSTITUTION' || user.getRoleKey() === 'TSRole_SACHBEARBEITER_TRAEGERSCHAFT') {
            $state.go('pendenzenInstitution');
        } else if (user.getRoleKey() === 'TSRole_SCHULAMT') {
            $state.go('faelle');
        } else if (user.getRoleKey() === 'TSRole_GESUCHSTELLER') {
            $state.go('gesuchstellerDashboard');
        } else {
            $state.go('gesuchstellerDashboard');
        }
    }
}
