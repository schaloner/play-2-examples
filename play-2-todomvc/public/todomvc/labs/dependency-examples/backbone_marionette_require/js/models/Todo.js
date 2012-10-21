
define(['backbone','lib/backbone-localStorage'],function(Backbone){
  'use strict';

  return Backbone.Model.extend({
    urlRoot: '/api/todo',
    defaults: {
      id:       null,
      title     : '',
      completed : false,
      created   : 0
    },
    initialize : function() {
      if (this.isNew()) this.set('created', Date.now());
    },
    toggle  : function() {
      return this.set('completed', !this.get('completed'));
    }
  });

});

