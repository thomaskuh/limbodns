var theApp = angular.module('theApp', []);

theApp.config(['$httpProvider', function($httpProvider) {
	  // Use x-www-form-urlencoded Content-Type
	  $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';

	  // Override $http service's default transformRequest
	  $httpProvider.defaults.transformRequest = [function(data) {
	    /**
	     * The workhorse; converts an object to x-www-form-urlencoded serialization.
	     * @param {Object} obj
	     * @return {String}
	     */ 
	    var param = function(obj) {
	      var query = '';
	      var name, value, fullSubName, subValue, innerObj, i;
	      
	      for(name in obj) {
	        value = obj[name];
	        
	        if(value instanceof Array) {
	          for(i=0; i<value.length; ++i) {
	            subValue = value[i];
	            fullSubName = name + '[' + i + ']';
	            innerObj = {};
	            innerObj[fullSubName] = subValue;
	            query += param(innerObj) + '&';
	          }
	        }
	        else if(value instanceof Object) {
	          for(subName in value) {
	            subValue = value[subName];
	            fullSubName = name + '[' + subName + ']';
	            innerObj = {};
	            innerObj[fullSubName] = subValue;
	            query += param(innerObj) + '&';
	          }
	        }
	        else if(value !== undefined && value !== null) {
	          query += encodeURIComponent(name) + '=' + encodeURIComponent(value) + '&';
	        }
	      }
	      
	      return query.length ? query.substr(0, query.length - 1) : query;
	    };
	    
	    return angular.isObject(data) && String(data) !== '[object File]' ? param(data) : data;
	  }];
	}]);


theApp.controller('MainCtrl', function ($scope,$http) {
	$scope.zones = null;
	
	$scope.newzone = new Object();
	$scope.newzone.name = '';
	$scope.newzone.nameserver = '';
	
	$scope.newrec = new Object();
	$scope.newrec.name = '';
	$scope.newrec.type = 'A';
	$scope.newrec.value = '';
	
	$scope.types = ["A", "AAAA"];
	
	$scope.uurl = new Object();
	$scope.uurl.auto = '';
	$scope.uurl.manu = '';
	$scope.uurl.wget = '';
	
	$scope.reloadZones = function() {
		$http.get("/api/zones").success(
			function(response) {
				$scope.zones = response;
			}
		);
	}
	
	$scope.recordDelete = function(valueRecord) {
		$http.post('/api/recordDelete', {id:valueRecord.id}).
			success(function(data, status, headers, config) {
				$scope.reloadZones();
			}).
			error(function(data, status, headers, config) {
				alert("Error: " + data);
			});
	}
	
	$scope.zoneDelete = function(valueZone) {
		$http.post('/api/zoneDelete', {name:valueZone.name}).
			success(function(data, status, headers, config) {
				$scope.reloadZones();
			}).
			error(function(data, status, headers, config) {
				alert("Error: " + data);
			});
	}
	
	$scope.zoneCreate = function() {
		$http.post('/api/zoneCreate', {name:$scope.newzone.name, nameserver:$scope.newzone.nameserver}).
		  success(function(data, status, headers, config) {
			  $scope.reloadZones();
			  $scope.newzone.name = '';
			  $scope.newzone.nameserver = '';
		  }).
		  error(function(data, status, headers, config) {
			  alert("Error: " + data);
		  });		
	}
	
	$scope.recordCreate = function(valueZone) {
		$http.post('/api/recordCreate', {zonename:valueZone.name, name:$scope.newrec.name, type:$scope.newrec.type, value:$scope.newrec.value}).
		  success(function(data, status, headers, config) {
			  $scope.reloadZones();
		  }).
		  error(function(data, status, headers, config) {
			  alert("Error: " + data);
		  });		
	}
	$scope.recordUpdate = function(valueRecord) {
		$http.post('/api/recordUpdate', {id:valueRecord.id, value:valueRecord.value}).
		  success(function(data, status, headers, config) {
			  $scope.reloadZones();
		  }).
		  error(function(data, status, headers, config) {
			  alert("Error: " + data);
		  });		
	}	
	
	$scope.showInfo = function(valueZone, valueRecord) {
		$scope.uurl.auto = window.location.protocol + "//" + window.location.host + "/update/" + valueRecord.id;
		if("A" == valueRecord.type) {
			$scope.uurl.manu = $scope.uurl.auto + "/127.0.0.1";
			$scope.uurl.wget = '--inet4-only';
		}
		if("AAAA" == valueRecord.type) {
			$scope.uurl.manu = $scope.uurl.auto + "/2001:0db8:85a3:0000:0000:8a2e:0370:7334";
			$scope.uurl.wget = '--inet6-only';
		}
		
		// window.location.protocol = "http:"
// 			window.location.host = "css-tricks.com"
			// window.location.pathname = "example/index.html"
		
		$('#myPupup').modal('show');
	}
	
	// Init modal:
	$('#myPupup').modal({
		show: false
	})

	$scope.reloadZones();
	
});

