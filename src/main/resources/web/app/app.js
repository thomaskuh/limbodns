var app = tkDefaultApp({authRouteAfterLogout: ['Login'], authUrlProbe: '/admin/probe'});

app.factory('client', ['$http', function($http) {
	return {
		probe: () => $http.get('/admin/probe'),
		zoneGets: () => $http.get('/admin/zones'),
		zoneGet: (id) => $http.get('/admin/zone/' + id),
		zoneCreate: (body) => $http.post('/admin/zone', body),
		zoneDelete: (id) => $http.delete('/admin/zone/' + id),
		recordGet: (zoneId, recordId) => $http.get('/admin/zone/' + zoneId + '/record/' + recordId),
		recordCreate: (zoneId, body) => $http.post('/admin/zone/' + zoneId + '/record', body),
		recordDelete: (zoneId, recordId) => $http.delete('/admin/zone/' + zoneId + '/record/' + recordId),
		recordUpdate: (zoneId, recordId, body) => $http.post('/admin/zone/' + zoneId + '/record/' + recordId, body)
	}
}]);

app.component('app', {
	templateUrl: 'app/app.html',
	bindings: { $router: '<' },
	$routeConfig: [
		{path: '/home', name: 'Home', component: 'pageHome', useAsDefault: true},
		{path: '/login', name: 'Login', component: 'tkLoginForm'},
		{path: '/zone/:zoneId', name: 'Zone', component: 'pageZone'},
		{path: '/zone/:zoneId/:recordId', name: 'Record', component: 'pageRecord'}
	],
	controller: function($scope, $location, $element, $rootRouter) {
		var ctrl = this;
		ctrl.path = '';

		$scope.$on('$routeChangeSuccess', function(scope, current, pre) {
			ctrl.path = $location.path();
		});
	}
});
