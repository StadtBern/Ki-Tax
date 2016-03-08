describe('applicationPropertyRS', function () {

    var applicationPropertyRS;
    var $httpBackend;
    var REST_API;
    var testKey = 'myTestKey';

    var mockApplicationProperty = {
        name: testKey,
        value: 'myTestValue'
    };

    beforeEach(module('ebeguWeb.admin'));

    beforeEach(inject(function (_applicationPropertyRS_, _$httpBackend_, _REST_API_) {
        applicationPropertyRS = _applicationPropertyRS_;
        $httpBackend = _$httpBackend_;
        REST_API = _REST_API_;
    }));

    // set the mock response
    beforeEach(function () {
        $httpBackend.when('GET', REST_API + 'application-properties/' + testKey).respond(mockApplicationProperty);
        $httpBackend.when('POST', REST_API + 'application-properties/' + testKey)
            .respond(201, mockApplicationProperty, {Location: 'http://localhost:8080/ebegu/api/v1/application-properties/test2'});

    });

    describe('Public API', function () {
        it('should include a getByKey() function', function () {
            expect(applicationPropertyRS.getByKey).toBeDefined();
        });
        it('should include a create() function', function () {
            expect(applicationPropertyRS.create).toBeDefined();
        });

    });

    describe('API Usage', function () {
        describe('getByKey', function () {

            it('should fetch property with given key', function () {
                $httpBackend.expectGET(REST_API + 'application-properties/' + testKey);
                var promise = applicationPropertyRS.getByKey(testKey);
                var property = null;

                promise.then(function (response) {
                    property = response.data;

                });
                $httpBackend.flush();
                expect(property).toEqual(mockApplicationProperty);

            });
            it('should create a property', function () {

            });
        });

        describe('create', function () {

            it('should create property with key and value', function () {
                $httpBackend.expectPOST(REST_API + 'application-properties/' + testKey, mockApplicationProperty.value);
                var promise = applicationPropertyRS.create(mockApplicationProperty.name, mockApplicationProperty.value);
                var property = null;

                promise.then(function (response) {
                    property = response.data;

                });
                $httpBackend.flush();
                expect(property).toEqual(mockApplicationProperty);

            });
        });

    });

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});
