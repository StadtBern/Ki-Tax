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
import ITranslateService = angular.translate.ITranslateService;
import {DvDialog} from '../../../core/directive/dv-dialog/dv-dialog';
import {RemoveDialogController} from '../../dialog/RemoveDialogController';
import IQService = angular.IQService;
import ErrorService from '../../../core/errors/service/ErrorService';
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

export class AbwesenheitViewController extends AbstractGesuchViewController {

    abwesenheitList: Array<AbwesenheitUI> = [];
    betreuungList: Array<KindBetreuungUI>;
    private removed: boolean;
    private changedBetreuungen: Array<TSBetreuung> = [];

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager', 'DvDialog',
        '$translate', '$q', 'ErrorService'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager, private DvDialog: DvDialog, private $translate: ITranslateService,
                private $q: IQService, private errorService: ErrorService) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
        this.removed = false;
        this.wizardStepManager.setCurrentStep(TSWizardStepName.ABWESENHEIT);
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
        this.abwesenheitList = [];
        this.betreuungList.forEach((kindBetreuung) => {
            if (kindBetreuung.betreuung.abwesenheitContainers) {
                kindBetreuung.betreuung.abwesenheitContainers.forEach((abwesenheitCont: TSAbwesenheitContainer) => {
                    this.abwesenheitList.push(new AbwesenheitUI(kindBetreuung, abwesenheitCont));
                });
            }
        });
    }

    public getBetreuungList(): Array<KindBetreuungUI> {
        return this.betreuungList;
    }

    public save(form: angular.IFormController): IPromise<Array<TSBetreuung>> {
        if (form.$valid) {
            this.errorService.clearAll();
            if (!form.$dirty && !this.removed) {
                // If there are no changes in form we don't need anything to update on Server and we could return the
                // promise immediately
                return this.$q.when([this.gesuchModelManager.getBetreuungToWorkWith()]);
            }

            //Zuerst loeschen wir alle Abwesenheiten jeder Betreuung
            let kinderList: Array<TSKindContainer> = this.gesuchModelManager.getKinderWithBetreuungList();
            kinderList.forEach((kindContainer: TSKindContainer) => {
                kindContainer.betreuungen.forEach((betreuung: TSBetreuung) => {
                    betreuung.abwesenheitContainers.length = 0;
                });
            });
            //Jetzt koennen wir alle geaenderten Abwesenheiten nochmal hinzufuegen
            this.abwesenheitList.forEach((abwesenheit: AbwesenheitUI) => {
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
            var remTitleText = this.$translate.instant('ABWESENHEIT_LOESCHEN');
            this.DvDialog.showDialog(removeDialogTemplate, RemoveDialogController, {
                title: remTitleText,
                deleteText: ''
            }).then(() => {   //User confirmed removal
                this.removeAbwesenheit(abwesenheit);
            });
        } else {
            this.removeAbwesenheit(abwesenheit);
        }
    }

    private removeAbwesenheit(abwesenheit: AbwesenheitUI) {
        let indexOf = this.abwesenheitList.lastIndexOf(abwesenheit);
        if (indexOf >= 0) {
            if (abwesenheit.kindBetreuung) {
                this.removed = true;
                this.addChangedBetreuungToList(abwesenheit.kindBetreuung.betreuung);
            }
            this.abwesenheitList.splice(indexOf, 1);
        }
    }

    public createAbwesenheit(): void {
        if (!this.abwesenheitList) {
            this.abwesenheitList = [];
        }
        this.abwesenheitList.push(new AbwesenheitUI(undefined, new TSAbwesenheitContainer()));
    }

    public getAbwesenheiten(): Array<AbwesenheitUI> {
        return this.abwesenheitList;
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
     * Diese Methode macht es moeglich, dass in einer Abwesenheit, das Betreuungsangebot geaendert werden kann. Damit fuegen wir die
     * Betreuung der Liste changedBetreuungen hinzu, damit sie danach aktualisiert wird
     */
    public changedAngebot(oldKindID: string, oldBetreuungID: string): void {
        this.gesuchModelManager.findKindById(oldKindID);
        this.gesuchModelManager.findBetreuungById(oldBetreuungID);
        if (this.gesuchModelManager.getBetreuungToWorkWith() && this.gesuchModelManager.getBetreuungToWorkWith().id) {
            this.addChangedBetreuungToList(this.gesuchModelManager.getBetreuungToWorkWith());
        }
    }
}
