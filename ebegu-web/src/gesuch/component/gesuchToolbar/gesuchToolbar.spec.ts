import {EbeguWebCore} from '../../../core/core.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import {GesuchToolbarController} from './gesuchToolbar';
import UserRS from '../../../core/service/userRS.rest';
import TSUser from '../../../models/TSUser';
import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';

describe('betreuungView', function () {

    let gesuchModelManager: GesuchModelManager;
    let gesuchToolbarController: GesuchToolbarController;
    let userRS: UserRS;
    let authServiceRS: AuthServiceRS;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        userRS = $injector.get('UserRS');
        authServiceRS = $injector.get('AuthServiceRS');
        gesuchToolbarController = new GesuchToolbarController(userRS, gesuchModelManager);
    }));

    describe('getVerantwortlicherFullName', () => {
        it('returns empty string for empty verantwortlicher', () => {
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('');
        });
        it('returns the fullname of the verantwortlicher', () => {
            let verantwortlicher: TSUser = new TSUser('Emiliano', 'Camacho');
            spyOn(authServiceRS, 'getPrincipal').and.returnValue(verantwortlicher);
            gesuchModelManager.initGesuch(true);
            expect(gesuchToolbarController.getVerantwortlicherFullName()).toEqual('Emiliano Camacho');
        });
    });
    describe('setVerantwortlicher()', () => {
        it('does nothing if the passed user is empty', () => {
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            gesuchToolbarController.setVerantwortlicher(undefined);
            expect(gesuchModelManager.setUserAsFallVerantwortlicher).not.toHaveBeenCalled();
        });
        it('sets the user as the verantwortlicher of the current fall', () => {
            spyOn(gesuchModelManager, 'setUserAsFallVerantwortlicher');
            let user: TSUser = new TSUser('Emiliano', 'Camacho');
            gesuchToolbarController.setVerantwortlicher(user);
            expect(gesuchModelManager.setUserAsFallVerantwortlicher).toHaveBeenCalledWith(user);
        });
    });

});
