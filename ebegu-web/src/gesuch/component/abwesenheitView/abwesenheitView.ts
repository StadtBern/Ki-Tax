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

import AbstractGesuchViewController from '../abstractGesuchView';
import {IComponentOptions, IPromise} from 'angular';
import GesuchModelManager from '../../service/gesuchModelManager';
import BerechnungsManager from '../../service/berechnungsManager';
import WizardStepManager from '../../service/wizardStepManager';
import {TSWizardStepName} from '../../../models/enums/TSWizardStepName';
import {TSWizardStepStatus} from '../../../models/enums/TSWizardStepStatus';
import TSBetreuung from '../../../models/TSBetreuung';
import TSAbwesenheitContainer from '../../../models/TSAbwesenheitContainer';
import TSKindContainer from '../../../models/TSKindContainer';
import {TSBetreuungsangebotTyp} from '../../../models/enums/TSBetreuungsangebotTyp';
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import ErrorService from '../../../core/errors/service/ErrorService';
import EbeguUtil from '../../../utils/EbeguUtil';
import ITranslateService = angular.translate.ITranslateService;
import IQService = angular.IQService;
import IScope = angular.IScope;
import ITimeoutService = angular.ITimeoutService;

let template = require('./abwesenheitView.html');
require('./abwesenheitView.less');
let removeDialogTemplate = require('../../dialog/removeDialogTemplate.html');

export class AbwesenheitViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = AbwesenheitViewController;
    controllerAs = 'vm';
}

export class KindBetreuungUI {
    public betreuung: TSBetreuung;
    public kind: TSKindContainer;
}

export class AbwesenheitUI {
    public kindBetreuung: KindBetreuungUI;
    public abwesenheit: TSAbwesenheitContainer;

    constructor(kindBetreuung: KindBetreuungUI, abwesenheit: TSAbwesenheitContainer) {
        this.kindBetreuung = kindBetreuung;
        this.abwesenheit = abwesenheit;
    }
}

export class AbwesenheitViewController extends AbstractGesuchViewController<Array<AbwesenheitUI>> {

    betreuungList: Array<KindBetreuungUI>;
    private removed: boolean;
    private changedBetreuungen: Array<TSBetreuung> = [];

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'DvDialog',
        '$translate', '$q', 'ErrorService', '$scope', '$timeout'];

    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private DvDialog: DvDialog, private $translate: ITranslateService,
                private $q: IQService, private errorService: ErrorService, $scope: IScope, $timeout: ITimeoutService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager, $scope, TSWizardStepName.ABWESENHEIT, $timeout);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.removed = false;
        this.wizardStepManager.updateCurrentWizardStepStatus(TSWizardStepStatus.OK);
        this.setBetreuungList();
        this.initAbwesenheitList();
    }

    /**
     * Aus der Liste mit den gesamten Kindern wird rausgefunden, welche Betreuungen TAGI oder KITA sind. Mit diesen
     * wird eine neue Liste gemacht, die ein Object fuer jedes Kind und Betreuung hat
     */
    private setBetreuungList(): void {
        let kinderList: Array<TSKindContainer> = this.gesuchModelManager.getKinderWithBetreuungList();
        this.betreuungList = [];
        kinderList.forEach((kind) => {
            let betreuungenFromKind: Array<TSBetreuung> = kind.betreuungen;
            betreuungenFromKind.forEach((betreuung) => {
                if (betreuung.institutionStammdaten && betreuung.institutionStammdaten.betreuungsangebotTyp &&
                    (betreuung.institutionStammdaten.betreuungsangebotTyp === TSBetreuungsangebotTyp.KITA
                        || betreuung.institutionStammdaten.betreuungsangebotTyp === TSBetreuungsangebotTyp.TAGESELTERN_KLEINKIND)) {
                    this.betreuungList.push({betreuung, kind});
                }
            });
        });
    }

    private initAbwesenheitList(): void {
        this.model = [];
        this.betreuungList.forEach((kindBetreuung) => {
            if (kindBetreuung.betreuung.abwesenheitContainers) {
                kindBetreuung.betreuung.abwesenheitContainers.forEach((abwesenheitCont: TSAbwesenheitContainer) => {
                    this.model.push(new AbwesenheitUI(kindBetreuung, abwesenheitCont));
                });
            }
        });
    }

    public getBetreuungList(): Array<KindBetreuungUI> {
        return this.betreuungList;
    }

    public save(): IPromise<Array<TSBetreuung>> {
        if (this.isGesuchValid()) {
            this.errorService.clearAll();
            if (!this.form.$dirty && !this.removed) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when([]);
            }

            //Zuerst loeschen wir alle Abwesenheiten jeder Betreuung
            let kinderList: Array<TSKindContainer> = this.gesuchModelManager.getKinderWithBetreuungList();
            kinderList.forEach((kindContainer: TSKindContainer) => {
                kindContainer.betreuungen.forEach((betreuung: TSBetreuung) => {
                    betreuung.abwesenheitContainers.length = 0;
                });
            });
            //Jetzt koennen wir alle geaenderten Abwesenheiten nochmal hinzufuegen
            this.model.forEach((abwesenheit: AbwesenheitUI) => {
                if (!abwesenheit.kindBetreuung.betreuung.abwesenheitContainers) {
                    abwesenheit.kindBetreuung.betreuung.abwesenheitContainers = [];
                }
                abwesenheit.kindBetreuung.betreuung.abwesenheitContainers.push(abwesenheit.abwesenheit);
                this.addChangedBetreuungToList(abwesenheit.kindBetreuung.betreuung);
            });

            return this.gesuchModelManager.updateBetreuungen(this.changedBetreuungen, true);
        }
        return undefined;
    }

    /**
     * Anhand des IDs schaut es ob die gegebene Betreuung bereits in der Liste changedBetreuungen ist.
     * Nur wenn sie noch nicht da ist, wird sie hinzugefuegt
     */
    private addChangedBetreuungToList(betreuung: TSBetreuung) {
        let betreuungAlreadyChanged: boolean = false;
        this.changedBetreuungen.forEach((changedBetreuung) => {
            if (changedBetreuung.id === betreuung.id) {
                betreuungAlreadyChanged = true;
            }
        });
        if (!betreuungAlreadyChanged) {
            this.changedBetreuungen.push(betreuung);
        }
    }

    /**
     * Nur wenn die Abwesenheit bereits existiert (in der DB) wird es nach Confirmation gefragt.
     * Sonst wird sie einfach geloescht
     */
    public removeAbwesenheitConfirm(abwesenheit: AbwesenheitUI): void {
        if (abwesenheit.abwesenheit.id) {
            let remTitleText = this.$translate.instant('ABWESENHEIT_LOESCHEN');
            this.DvDialog.showRemoveDialog(removeDialogTemplate, this.form, RemoveDialogController, {
                title: remTitleText,
                deleteText: '',
                parentController: undefined,
                elementID: undefined
            }).then(() => {   //User confirmed removal
                this.removeAbwesenheit(abwesenheit);
            });
        } else {
            this.removeAbwesenheit(abwesenheit);
        }
    }

    private removeAbwesenheit(abwesenheit: AbwesenheitUI) {
        let indexOf = this.model.lastIndexOf(abwesenheit);
        if (indexOf >= 0) {
            if (abwesenheit.kindBetreuung) {
                this.removed = true;
                this.addChangedBetreuungToList(abwesenheit.kindBetreuung.betreuung);
            }
            this.model.splice(indexOf, 1);
            this.$timeout(() => EbeguUtil.selectFirst(), 100);
        }
    }

    public createAbwesenheit(): void {
        if (!this.model) {
            this.model = [];
        }
        this.model.push(new AbwesenheitUI(undefined, new TSAbwesenheitContainer()));
        this.$postLink();
        //todo focus on specific id, so the newly added abwesenheit will be selected not the first in the DOM
    }

    public getAbwesenheiten(): Array<AbwesenheitUI> {
        return this.model;
    }

    /**
     * Gibt ein string zurueck mit der Form
     * "Kindname - InstitutionName"
     * Leerer String wieder zurueckgeliefert wenn die Daten nicht richtig sind
     */
    public getTextForBetreuungDDL(kindBetreuung: KindBetreuungUI): string {
        if (kindBetreuung && kindBetreuung.kind && kindBetreuung.kind.kindJA
            && kindBetreuung.betreuung && kindBetreuung.betreuung.institutionStammdaten && kindBetreuung.betreuung.institutionStammdaten.institution) {

            return kindBetreuung.kind.kindJA.getFullName() + ' - ' + kindBetreuung.betreuung.institutionStammdaten.institution.name;
        }
        return '';
    }

    /**
     * Diese Methode macht es moeglich, dass in einer Abwesenheit, das Betreuungsangebot geaendert werden kann. Damit
     * fuegen wir die Betreuung der Liste changedBetreuungen hinzu, damit sie danach aktualisiert wird
     */
    public changedAngebot(oldKindID: string, oldBetreuungID: string): void {
        // In case the Abwesenheit didn't exist before, the old IDs will be empty and there is no need to change
        // anything
        if (oldKindID && oldKindID !== '' && oldBetreuungID && oldBetreuungID !== '') {
            this.gesuchModelManager.findKindById(oldKindID);
            this.gesuchModelManager.findBetreuungById(oldBetreuungID);
            let betreuungToWorkWith: TSBetreuung = this.gesuchModelManager.getBetreuungToWorkWith();
            if (betreuungToWorkWith && betreuungToWorkWith.id) {
                this.addChangedBetreuungToList(betreuungToWorkWith);
            }
        }
    }

    public getPreviousButtonText(): string {
        if (this.getAbwesenheiten().length === 0) {
            return 'ZURUECK_ONLY_UPPER';
        }
        return 'ZURUECK_UPPER';
    }

    public getNextButtonText(): string {
        if (this.getAbwesenheiten().length === 0) {
            return 'WEITER_ONLY_UPPER';
        }
        return 'WEITER_UPPER';
    }
}
