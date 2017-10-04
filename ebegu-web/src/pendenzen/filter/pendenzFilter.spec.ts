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

import {EbeguWebPendenzen} from '../pendenzen.module';
import TSAntragDTO from '../../models/TSAntragDTO';
import {TSAntragTyp} from '../../models/enums/TSAntragTyp';
import TSGesuchsperiode from '../../models/TSGesuchsperiode';
import {TSBetreuungsangebotTyp} from '../../models/enums/TSBetreuungsangebotTyp';
import {TSDateRange} from '../../models/types/TSDateRange';
import * as moment from 'moment';
import {TSAntragStatus} from '../../models/enums/TSAntragStatus';
import {TSGesuchsperiodeStatus} from '../../models/enums/TSGesuchsperiodeStatus';

describe('pendenzFilter', function () {

    let pendenzFilter: any;
    let pendenzArray: Array<TSAntragDTO>;
    let pendenz1: TSAntragDTO;
    let pendenz2: TSAntragDTO;
    let pendenz3: TSAntragDTO;
    let gesuchsperiode: TSGesuchsperiode;

    beforeEach(angular.mock.module(EbeguWebPendenzen.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        pendenzFilter = $injector.get('$filter')('pendenzFilter');

        let ab = moment('31.08.2016', 'DD.MM.YYYY');
        let bis = moment('01.07.2017', 'DD.MM.YYYY');
        gesuchsperiode = new TSGesuchsperiode(TSGesuchsperiodeStatus.AKTIV, new TSDateRange(ab, bis));

        pendenzArray = [];
        pendenz1 = new TSAntragDTO('id1', 1, 'Hernandez', TSAntragTyp.ERSTGESUCH, ab, ab, undefined,
            [TSBetreuungsangebotTyp.KITA], ['Instit1'], 'Juan Arbolado', TSAntragStatus.IN_BEARBEITUNG_JA,
            gesuchsperiode.gueltigkeit.gueltigAb, gesuchsperiode.gueltigkeit.gueltigBis);
        pendenzArray.push(pendenz1);

        pendenz2 = new TSAntragDTO('id2', 2, 'Perez', TSAntragTyp.ERSTGESUCH, ab, ab, undefined,
            [TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND], ['Instit2'], 'Antonio Jimenez', TSAntragStatus.IN_BEARBEITUNG_JA,
            gesuchsperiode.gueltigkeit.gueltigAb, gesuchsperiode.gueltigkeit.gueltigBis);
        pendenzArray.push(pendenz2);

        pendenz3 = new TSAntragDTO('id3', 3, 'Dominguez', TSAntragTyp.MUTATION, ab, ab, undefined,
            [TSBetreuungsangebotTyp.KITA, TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND], ['Instit1', 'Instit2'],
            'Eustaquio Romualdo', TSAntragStatus.IN_BEARBEITUNG_JA,
            gesuchsperiode.gueltigkeit.gueltigAb, gesuchsperiode.gueltigkeit.gueltigBis);
        pendenzArray.push(pendenz3);

    }));

    describe('API usage', function () {
        it('should return an array with only the element with the given Fallnummer', function () {
            expect(pendenzFilter(pendenzArray, {fallNummer: '1'})).toEqual([pendenz1]);
            expect(pendenzFilter(pendenzArray, {fallNummer: '01'})).toEqual([pendenz1]);
            expect(pendenzFilter(pendenzArray, {fallNummer: '0002'})).toEqual([pendenz2]);
            expect(pendenzFilter(pendenzArray, {fallNummer: '4'})).toEqual([]); // the fallnummer doesn't exist
        });
        it('should return an array with only the elements with the given Familienname or containing the given string', function () {
            expect(pendenzFilter(pendenzArray, {familienName: 'Hernandez'})).toEqual([pendenz1]);
            expect(pendenzFilter(pendenzArray, {familienName: 'ez'})).toEqual([pendenz1, pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {familienName: ''})).toEqual([pendenz1, pendenz2, pendenz3]); // empty string returns all elements
            expect(pendenzFilter(pendenzArray, {familienName: 'rrr'})).toEqual([]); // no familienname with this pattern
        });
        it('should return an array with only the elements of the given antragTyp', function () {
            expect(pendenzFilter(pendenzArray, {antragTyp: TSAntragTyp.ERSTGESUCH})).toEqual([pendenz1, pendenz2]);
            expect(pendenzFilter(pendenzArray, {antragTyp: TSAntragTyp.MUTATION})).toEqual([pendenz3]);
            expect(pendenzFilter(pendenzArray, {antragTyp: ''})).toEqual([pendenz1, pendenz2, pendenz3]); // empty string returns all elements
            expect(pendenzFilter(pendenzArray, {antragTyp: 'error'})).toEqual([]);
        });
        it('should return an array with only the elements of the given gesuchsperiodeGueltigAb', function () {
            expect(pendenzFilter(pendenzArray, {gesuchsperiodeGueltigAb: '31.08.2016'})).toEqual([pendenz1, pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {gesuchsperiodeGueltigAb: ''})).toEqual([pendenz1, pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {gesuchsperiodeGueltigAb: '2020/2021'})).toEqual([]);
        });
        it('should return an array with only the elements of the given eingangsdatum', function () {
            expect(pendenzFilter(pendenzArray, {eingangsdatum: '31.08.2016'})).toEqual([pendenz1, pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {eingangsdatum: ''})).toEqual([pendenz1, pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {eingangsdatum: '31.08.2017'})).toEqual([]);
        });
        it('should return an array with only the elements of the given angebotstyp', function () {
            expect(pendenzFilter(pendenzArray, {angebote: TSBetreuungsangebotTyp.KITA})).toEqual([pendenz1, pendenz3]);
            expect(pendenzFilter(pendenzArray, {angebote: TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND})).toEqual([pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {angebote: TSBetreuungsangebotTyp.TAGESSCHULE})).toEqual([]);
            expect(pendenzFilter(pendenzArray, {angebote: ''})).toEqual([pendenz1, pendenz2, pendenz3]);
        });
        it('should return an array with only the elements of the given institutionen', function () {
            expect(pendenzFilter(pendenzArray, {institutionen: 'Instit1'})).toEqual([pendenz1, pendenz3]);
            expect(pendenzFilter(pendenzArray, {institutionen: 'Instit2'})).toEqual([pendenz2, pendenz3]);
            expect(pendenzFilter(pendenzArray, {institutionen: ''})).toEqual([pendenz1, pendenz2, pendenz3]);
        });
        it('should return the elements containing all given params, for a multiple filtering', function () {
            expect(pendenzFilter(pendenzArray, {
                familienName: 'Hernandez',
                institutionen: 'Instit1'
            })).toEqual([pendenz1]);
        });
    });

});
