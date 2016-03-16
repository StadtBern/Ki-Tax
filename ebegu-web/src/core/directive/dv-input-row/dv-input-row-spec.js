describe('dvInputRow', function () {

    beforeEach(module('ebeguWeb.core', 'src/core/directive/dv-input-row/dv-input-row.html'));

    var $compile, $rootScope;

    beforeEach(inject(function (_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('should ...', function () {

        /*
         To test your directive, you need to create some html that would use your directive,
         send that through compile() then compare the results.

         var element = $compile('<dv-input-row></dv-input-row>')($rootScope);
         $rootScope.$digest();

         expect(element.text()).toBe('hello, world');
         */

    });
});
