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

import {EbeguWebGesuch} from '../../gesuch.module';
import {DVFinanzielleSituationRequireController} from './dv-finanzielle-situation-require';

describe('finanzielleSituationRequire', function () {

    beforeEach(angular.mock.module(EbeguWebGesuch.name));

    let component: any;
    let scope: angular.IScope;
    let $componentController: angular.IComponentControllerService;
    let controller: DVFinanzielleSituationRequireController;

    beforeEach(angular.mock.inject(function ($injector: angular.auto.IInjectorService) {
        $componentController = $injector.get('$componentController');
        controller = new DVFinanzielleSituationRequireController();
    }));

    it('should be defined', function () {
        let bindings: {};
        component = $componentController('dvFinanzielleSituationRequire', {$scope: scope}, bindings);
        expect(component).toBeDefined();
    });

    describe('Test for boolean finanzielleSituationRequired', () => {
        it('should be true when nothing is set', () => {
            controller.setFinanziellesituationRequired();
            expect(controller.finanzielleSituationRequired).toBe(true);
        });
        it('should be true when areThereOnlySchulamtangebote is false', () => {
            controller.areThereOnlySchulamtangebote = false;
            controller.setFinanziellesituationRequired();
            expect(controller.finanzielleSituationRequired).toBe(true);
        });
        it('should be true when areThereOnlySchulamtangebote is false, not sozialhilfeBezueger and verguenstigungGewuenscht', () => {
            controller.areThereOnlySchulamtangebote = false;
            controller.sozialhilfeBezueger = false;
            controller.verguenstigungGewuenscht = true;
            controller.setFinanziellesituationRequired();
            expect(controller.finanzielleSituationRequired).toBe(true);
        });
        it('should be true when areThereOnlySchulamtangebote is false, not sozialhilfeBezueger and verguenstigungGewuenscht', () => {
            controller.areThereOnlySchulamtangebote = false;
            controller.sozialhilfeBezueger = false;
            controller.verguenstigungGewuenscht = false;
            controller.setFinanziellesituationRequired();
            expect(controller.finanzielleSituationRequired).toBe(true);
        });
    });
});
