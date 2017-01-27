import {EbeguWebPosteingang} from '../posteingang.module';
import * as moment from 'moment';
import TSMitteilung from '../../models/TSMitteilung';
import TSUser from '../../models/TSUser';
import TSFall from '../../models/TSFall';
import {TSMitteilungTeilnehmerTyp} from '../../models/enums/TSMitteilungTeilnehmerTyp';
import {TSMitteilungStatus} from '../../models/enums/TSMitteilungStatus';

describe('posteingangFilter', function () {

    let posteingangFilter: any;
    let mitteilungArray: Array<TSMitteilung> = [];
    let mitteilung1: TSMitteilung;
    let mitteilung2: TSMitteilung;
    let mitteilung3: TSMitteilung;
    let mitteilung4: TSMitteilung;

    beforeEach(angular.mock.module(EbeguWebPosteingang.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        posteingangFilter = $injector.get('$filter')('posteingangFilter');

        let ja = new TSUser();
        ja.nachname = 'Blaser';
        ja.vorname = 'Kurt';

        let gesuchsteller1 = new TSUser();
        gesuchsteller1.nachname = 'Berger';
        gesuchsteller1.vorname = 'Michael';
        let fall1 = new TSFall();
        fall1.fallNummer = 112;
        fall1.besitzer = gesuchsteller1;

        let gesuchsteller2 = new TSUser();
        gesuchsteller2.nachname = 'Gerber';
        gesuchsteller2.vorname = 'Emma';
        let fall2 = new TSFall();
        fall2.fallNummer = 108;
        fall2.besitzer = gesuchsteller2;

        mitteilungArray = [];

        mitteilung1 = new TSMitteilung(fall1, TSMitteilungTeilnehmerTyp.GESUCHSTELLER, TSMitteilungTeilnehmerTyp.JUGENDAMT, gesuchsteller1,
            null, 'Frage zum IAM', 'Warum ist die Banane krumm?', TSMitteilungStatus.NEU, moment('2016-01-01'));
        mitteilungArray.push(mitteilung1);

        mitteilung2 = new TSMitteilung(fall1, TSMitteilungTeilnehmerTyp.GESUCHSTELLER, TSMitteilungTeilnehmerTyp.JUGENDAMT, gesuchsteller1,
            ja, 'Adressänderung', 'Unsere neue Adresse lautet...', TSMitteilungStatus.NEU, moment('2016-02-02'));
        mitteilungArray.push(mitteilung2);

        mitteilung3 = new TSMitteilung(fall2, TSMitteilungTeilnehmerTyp.GESUCHSTELLER, TSMitteilungTeilnehmerTyp.JUGENDAMT, gesuchsteller2,
            ja, 'Frage zu Dokumentupload', 'Welche Dokumente kann ich...', TSMitteilungStatus.NEU, moment('2016-03-03'));
        mitteilungArray.push(mitteilung3);

        mitteilung4 = new TSMitteilung(fall2, TSMitteilungTeilnehmerTyp.GESUCHSTELLER, TSMitteilungTeilnehmerTyp.JUGENDAMT, gesuchsteller2,
            ja, 'Gesuch freigegeben', 'Was nun?', TSMitteilungStatus.NEU, moment('2016-02-02'));
        mitteilungArray.push(mitteilung4);

    }));

    describe('API usage', function () {
        it('should return an array with only the elements with the given Sender', function () {
            expect(posteingangFilter(mitteilungArray, {sender: 'Berger'})).toEqual([mitteilung1, mitteilung2]);
            expect(posteingangFilter(mitteilungArray, {sender: 'er'})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {sender: ''})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {sender: 'rrr'})).toEqual([]); // no familienname with this pattern
        });
        it('should return an array with only the element with the given Fallnummer', function () {
            expect(posteingangFilter(mitteilungArray, {'fall': {'fallNummer': '000'}})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'fallNummer': '0001'}})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'fallNummer': '1'}})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'fallNummer': '12'}})).toEqual([mitteilung1, mitteilung2]);
        });
        it('should return an array with only the elements with the given Familie (Besitzer)', function () {
            expect(posteingangFilter(mitteilungArray, {'fall': {'besitzer': 'Berger'}})).toEqual([mitteilung1, mitteilung2]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'besitzer': 'er'}})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'besitzer': ''}})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {'fall': {'besitzer': 'rrr'}})).toEqual([]);
        });
        it('should return an array with only the elements with the given subject', function () {
            expect(posteingangFilter(mitteilungArray, {subject: 'Frage'})).toEqual([mitteilung1, mitteilung3]);
            expect(posteingangFilter(mitteilungArray, {subject: 'Dok'})).toEqual([mitteilung2]);
            expect(posteingangFilter(mitteilungArray, {subject: ''})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {subject: 'rrr'})).toEqual([]); // no familienname with this pattern
        });
        it('should return an array with only the elements of the given sentDatum', function () {
            expect(posteingangFilter(mitteilungArray, {sentDatum: '2016-02-02'})).toEqual([mitteilung2, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {sentDatum: ''})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {sentDatum: '2016-05-05'})).toEqual([]);
        });
        it('should return an array with only the elements of the given verantwortlicher', function () {
            expect(posteingangFilter(mitteilungArray, {verantwortlicher: 'Blaser'})).toEqual([mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {verantwortlicher: 'ser'})).toEqual([mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {verantwortlicher: ''})).toEqual([mitteilung1, mitteilung2, mitteilung3, mitteilung4]);
            expect(posteingangFilter(mitteilungArray, {verantwortlicher: 'rrr'})).toEqual([]);
        });
    });
});
