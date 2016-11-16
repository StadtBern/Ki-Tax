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
let template = require('./abwesenheitView.html');
require('./abwesenheitView.less');


export class AbwesenheitViewComponentConfig implements IComponentOptions {
    transclude = false;
    bindings: any = {};
    template = template;
    controller = AbwesenheitViewController;
    controllerAs = 'vm';
}

class KindBetreuung {
    public betreuung: TSBetreuung;
    public kind: TSKindContainer;
}

class AbwesenheitUI {
    public kindBetreuung: KindBetreuung;
    public abwesenheit: TSAbwesenheitContainer;

    constructor(kindBetreuung: KindBetreuung, abwesenheit: TSAbwesenheitContainer) {
        this.kindBetreuung = kindBetreuung;
        this.abwesenheit = abwesenheit;
    }
}

export class AbwesenheitViewController extends AbstractGesuchViewController {

    abwesenheitList: Array<AbwesenheitUI> = [];
    betreuungList: Array<KindBetreuung>;

    static $inject = ['GesuchModelManager', 'BerechnungsManager', 'WizardStepManager'];
    /* @ngInject */
    constructor(gesuchModelManager: GesuchModelManager, berechnungsManager: BerechnungsManager,
                wizardStepManager: WizardStepManager) {

        super(gesuchModelManager, berechnungsManager, wizardStepManager);
        this.initViewModel();
    }

    private initViewModel(): void {
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

    public getBetreuungList(): Array<KindBetreuung> {
        return this.betreuungList;
    }

    public save(form: angular.IFormController): IPromise<TSBetreuung> {
        if (form.$valid) {
            this.abwesenheitList.forEach((newAbwesenheit: AbwesenheitUI) => {
                if (!newAbwesenheit.kindBetreuung.betreuung.abwesenheitContainers) {
                    newAbwesenheit.kindBetreuung.betreuung.abwesenheitContainers = [];
                }
                newAbwesenheit.kindBetreuung.betreuung.abwesenheitContainers.push(newAbwesenheit.abwesenheit);
                this.gesuchModelManager.findKind(newAbwesenheit.kindBetreuung.kind);
                this.gesuchModelManager.findBetreuung(newAbwesenheit.kindBetreuung.betreuung);
                this.gesuchModelManager.updateBetreuung();
                //     .then(() => {
                //     this.abwesenheitList = [];
                // });
            })
        }
        return undefined;
    }

    public createAbwesenheit(): void {
        if (!this.abwesenheitList) {
            this.abwesenheitList = [];
        }
        this.abwesenheitList.push(new AbwesenheitUI(null, new TSAbwesenheitContainer()));
    }

    public getAbwesenheiten(): Array<AbwesenheitUI> {
        return this.abwesenheitList;
    }

    /**
     * Gibt ein string zurueck mit der Form
     * "Kindname - InstitutionName"
     * Leerer String wieder zurueckgeliefert wenn die Daten nicht richtig sind
     */
    public getTextForBetreuungDDL(kindBetreuung: KindBetreuung): string {
        if (kindBetreuung && kindBetreuung.kind && kindBetreuung.kind.kindJA
            && kindBetreuung.betreuung && kindBetreuung.betreuung.institutionStammdaten && kindBetreuung.betreuung.institutionStammdaten.institution) {

            return kindBetreuung.kind.kindJA.getFullName() + " - " + kindBetreuung.betreuung.institutionStammdaten.institution.name;
        }
        return '';
    }
}
