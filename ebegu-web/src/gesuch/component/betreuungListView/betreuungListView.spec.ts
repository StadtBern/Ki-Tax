/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import AuthServiceRS from '../../../authentication/service/AuthServiceRS.rest';
import {EbeguWebCore} from '../../../core/core.module';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import ErrorService from '../../../core/errors/service/ErrorService';
import TSKindContainer from '../../../models/TSKindContainer';
import EbeguUtil from '../../../utils/EbeguUtil';
import {EbeguWebGesuch} from '../../gesuch.module';
import GesuchModelManager from '../../service/gesuchModelManager';
import WizardStepManager from '../../service/wizardStepManager';
import {BetreuungListViewController} from './betreuungListView';

describe('betreuungListViewTest', function () {

    let betreuungListView: BetreuungListViewController;
    let gesuchModelManager: GesuchModelManager;
    let $state: angular.ui.IStateService;

    beforeEach(angular.mock.module(EbeguWebCore.name));
    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        gesuchModelManager = $injector.get('GesuchModelManager');
        let wizardStepManager: WizardStepManager = $injector.get('WizardStepManager');
        spyOn(wizardStepManager, 'updateWizardStepStatus').and.returnValue({});
        spyOn(gesuchModelManager, 'convertKindNumberToKindIndex').and.returnValue(0);
        $state = $injector.get('$state');
        let $translate: angular.translate.ITranslateService = $injector.get('$translate');
        let dialog: DvDialog = $injector.get('DvDialog');
        let ebeguUtil: EbeguUtil = $injector.get('EbeguUtil');
        let errorService: ErrorService = $injector.get('ErrorService');
        let $timeout = $injector.get('$timeout');
        let authServiceRS: AuthServiceRS = $injector.get('AuthServiceRS');

        betreuungListView = new BetreuungListViewController($state, gesuchModelManager, $translate, dialog, ebeguUtil, undefined,
            errorService, wizardStepManager, authServiceRS, $injector.get('$rootScope'), undefined, $timeout);
    }));

    describe('Public API', function () {
        it('should include a createBetreuung() function', function () {
            expect(betreuungListView.createBetreuung).toBeDefined();
        });
    });

    describe('API Usage', function () {
        describe('createBetreuung', () => {
            it('should create a Betreuung', () => {
                let tsKindContainer = new TSKindContainer();
                tsKindContainer.betreuungen = [];
                tsKindContainer.kindNummer = 1;
                spyOn($state, 'go');
                spyOn(gesuchModelManager, 'findKind').and.returnValue(0);

                betreuungListView.createBetreuung(tsKindContainer);

                expect(gesuchModelManager.getKindIndex()).toBe(0);

                expect($state.go).toHaveBeenCalledWith('gesuch.betreuung', {
                    betreuungNumber: undefined,
                    kindNumber: 1,
                    gesuchId: ''
                });
            });
        });
    });

});
