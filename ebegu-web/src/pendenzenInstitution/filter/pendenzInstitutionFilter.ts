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

// Es wird empfohlen, Filters als normale Funktionen zu implementieren, denn es bringt nichts, dafuer eine Klasse zu implementieren.
import EbeguUtil from '../../utils/EbeguUtil';
PendenzInstitutionFilter.$inject = ['$filter', 'EbeguUtil'];

// Zuerst pruefen wir welcher Wert kommt, d.h. aus welcher Column. Je nach Column wird danach dem entsprechenden Comparator aufgerufen.
// Fuer mehrere Columns reicht es mit dem standard Comparator, der auch hier einfach implementiert wird.
export function PendenzInstitutionFilter($filter: any, ebeguUtil: EbeguUtil) {

    let filterFilter = $filter('filter');
    let dateFilter = $filter('date');

    let standardComparator = function standardComparator(obj: any, text: any) {
        text = ('' + text).toLowerCase();
        return ('' + obj).toLowerCase().indexOf(text) > -1;
    };

    return (array: any, expression: any) => {

        function customComparator(actual: any, expected: any) {
            if (expression.institution && expression.institution === expected) {
                if (actual) {
                    return actual.name === expected;
                }
            }
            if (expression.eingangsdatum && expression.eingangsdatum === expected) {
                let actualDate = dateFilter(new Date(actual), 'dd.MM.yyyy');
                return actualDate === expected;
            }
            if (expression.geburtsdatum && expression.geburtsdatum === expected) {
                let actualDate = dateFilter(new Date(actual), 'dd.MM.yyyy');
                return actualDate === expected;
            }
            return standardComparator(actual, expected);
        }
        return filterFilter(array, expression, customComparator);
    };
}
