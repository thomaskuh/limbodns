app.component('pageZone', {
  templateUrl: 'app/pageZone.html',
  bindings: {},
  controller: function(client) {
	  var ctrl = this;
	  
	  ctrl.types = ["A", "AAAA", "CNAME", "MX", "TXT"];
	  
	  ctrl.zoneId = null;
	  ctrl.zone = null;
	  ctrl.recordNew = {name: 'www', type: ctrl.types[0], value: '127.0.0.1', token: ''};
	  
	  ctrl.$routerOnActivate = function(next, previous) {
		  ctrl.zoneId = next.params.zoneId;
		  ctrl.reload();
	  };
	  
	  ctrl.reload = function() {
		  client.zoneGet(ctrl.zoneId).then((resp) => {
			  ctrl.zone = resp.data;
			  ctrl.recordEdit = null;
		  });
	  };
      
	  ctrl.safeTtl = function(val) {
		var ttl = parseInt(val);
		return (ttl == NaN || ttl < 0) ? null : ttl;
	  };
	  
	  ctrl.recordCreate = function() {
		ctrl.recordNew.ttl = ctrl.safeTtl(ctrl.recordNew.ttl);
		  client.recordCreate(ctrl.zoneId, ctrl.recordNew).then((resp) => {
        ctrl.zone.records.push(resp.data);
      });
    };

	  ctrl.recordDelete = function(val) {
		  client.recordDelete(ctrl.zoneId, val.id).then((resp) => {
        var idx = ctrl.zone.records.indexOf(val);
        ctrl.zone.records.splice(idx, 1);
      });
    };

	  ctrl.recordSave = function(val) {
		val.ttl = ctrl.safeTtl(val.ttl);
		  client.recordUpdate(ctrl.zoneId, val.id, val).then((resp) => {
        var idx = ctrl.zone.records.indexOf(val);
        ctrl.zone.records[idx] = resp.data;
      });
	  };

	  ctrl.recordUndo = function(val) {
		  client.recordGet(ctrl.zoneId, val.id).then((resp) => {
		    var idx = ctrl.zone.records.indexOf(val);
		    ctrl.zone.records[idx] = resp.data;
		  });
	  };
	  
	  ctrl.generateToken = function(val) {
		  val.token = Math.random().toString(36).substring(2, 12);
		  val.dirty = true
	  };
	  
	  ctrl.recordNewChanged = function() {
		 if('CNAME' == ctrl.recordNew.type) {
			 ctrl.recordNew.token = '';
			 ctrl.recordNew.value = 'target.example.org.';
		 }
		 else if('A' == ctrl.recordNew.type) {
			 ctrl.recordNew.value = '127.0.0.1';
		 }
		 else if('AAAA' == ctrl.recordNew.type) {
			 ctrl.recordNew.value = '0:0:0:0:0:0:0:1';
		 }
		 else if('MX' == ctrl.recordNew.type) {
             ctrl.recordNew.token = '';
             ctrl.recordNew.value = 'mailserver.example.org.';
         }
         else if('TXT' == ctrl.recordNew.type) {
             ctrl.recordNew.token = '';
             ctrl.recordNew.value = 'some random text';
         }
	  };
	  
  }
});
