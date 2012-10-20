var app = app || {};

(function() {
	'use strict';

	// Todo Model
	// ----------

	// Our basic **Todo** model has `title`, `order`, and `completed` attributes.
	app.Todo = Backbone.Model.extend({

        // Steve: urlRoot added to support RESTful interactions
        urlRoot: '/api/todo',

		// Default attributes for the todo
		// and ensure that each todo created has `title` and `completed` keys.
		defaults: {
            // Steve: id added because Backbone requires it for server interactions
            id:null,
			title: '',
			completed: false
		},

		// Toggle the `completed` state of this todo item.
		toggle: function() {
			this.save({
				completed: !this.get('completed')
			});
		}

	});

}());
