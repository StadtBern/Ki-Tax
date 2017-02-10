import {EbeguWebCore} from '../../core.module';
import {ValueinputController} from './dv-valueinput';
import INgModelController = angular.INgModelController;

describe('dvValueinput', function () {

    let controller: ValueinputController;

    beforeEach(angular.mock.module(EbeguWebCore.name));

    beforeEach(angular.mock.inject(function ($injector: any) {
        controller = new ValueinputController();
        controller.ngModelCtrl = <any> {
            $modelValue: undefined,
            // renderCalled: false,
            $setViewValue: function(passedValue: any) {
                this.$modelValue = passedValue;
            },
            $render: function() {
                return; // this.renderCalled = true;
            }
        };
    }));

    describe('removeNotDigits', function () {
        it('should return a number from a number', function () {
            controller.valueinput = '1234';
            controller.ngModelCtrl.$setViewValue('1234');
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('1234');
        });
        it('should return a number removing leading zeros', function () {
            controller.valueinput = '00123400';
            controller.ngModelCtrl.$setViewValue('00123400');
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('123400');
        });
        it('should return a number removing text', function () {
            controller.valueinput = '1r2f3,4.5';
            controller.ngModelCtrl.$setViewValue('1r2f3,4.5');
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('12345');
        });
        it('should return a number removing whitespaces', function () {
            controller.valueinput = '  1234';
            controller.ngModelCtrl.$setViewValue('  1234');
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('1234');
        });
        it('should return a negative number when negative allowed', function () {
            controller.valueinput = '-1234';
            controller.ngModelCtrl.$setViewValue('-1234');
            controller.allowNegative = true;
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('-1234');
        });
        it('should return a negative number when negative allowed but minus character repeated', function () {
            controller.valueinput = '-12-34';
            controller.ngModelCtrl.$setViewValue('-12-34');
            controller.allowNegative = true;
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('-1234');
        });
        it('should return a positive number when negative not allowed', function () {
            controller.valueinput = '-1234';
            controller.ngModelCtrl.$setViewValue('-1234');
            controller.removeNotDigits();
            expect(controller.ngModelCtrl.$modelValue).toBe('1234');
        });
    });

});
