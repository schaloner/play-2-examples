/*global jQuery, Handlebars */
jQuery(function( $ ) {
	'use strict';

	var Utils = {
		// https://gist.github.com/1308368
		pluralize: function( count, word ) {
			return count === 1 ? word : word + 's';
		},
        getAll: function() {
            return $.getJSON('/api/todo',
                function(data) {
                    return data;
                });
        },
        save: function(toDo) {
            return $.ajax({
                type: 'POST',
                url: '/api/todo',
                data: JSON.stringify(toDo),
                dataType: 'json',
                contentType:'application/json'
            });
        },
        update: function(toDo) {
            return $.ajax({
                type: 'PUT',
                url: '/api/todo/' + toDo.id,
                data: JSON.stringify(toDo),
                dataType: 'json',
                contentType:'application/json'
            });
        },
        updateAll: function(toDos) {
            var response;
            $.each(toDos, function(index, toDo) {
                // get the last response
                response = Utils.update(toDo);
            });
            return response;
        },
        delete: function(data) {
            return $.ajax({
                type: 'DELETE',
                url: '/api/todo/' + data.id,
                dataType: 'json'
            });
        }
    };

	var App = {
		init: function() {
			this.ENTER_KEY = 13;
            var self = this;
            Utils.getAll()
                .success(function(data) {
                    self.todos = data
                })
                .error(function()
                {
                    self.todos = []
                })
                .complete(function() {
                    self.render();
                });
			this.cacheElements();
			this.bindEvents();
		},
		cacheElements: function() {
			this.todoTemplate = Handlebars.compile( $('#todo-template').html() );
			this.footerTemplate = Handlebars.compile( $('#footer-template').html() );
			this.$todoApp = $('#todoapp');
			this.$newTodo = $('#new-todo');
			this.$toggleAll = $('#toggle-all');
			this.$main = $('#main');
			this.$todoList = $('#todo-list');
			this.$footer = this.$todoApp.find('#footer');
			this.$count = $('#todo-count');
			this.$clearBtn = $('#clear-completed');
		},
		bindEvents: function() {
			var list = this.$todoList;
			this.$newTodo.on( 'keyup', this.create );
			this.$toggleAll.on( 'change', this.toggleAll );
			this.$footer.on( 'click', '#clear-completed', this.destroyCompleted );
			list.on( 'change', '.toggle', this.toggle );
			list.on( 'dblclick', 'label', this.edit );
			list.on( 'keypress', '.edit', this.blurOnEnter );
			list.on( 'blur', '.edit', this.update );
			list.on( 'click', '.destroy', this.destroy );
		},
		render: function() {
            var self = this;
            Utils.getAll()
                .success(function(data) {
                    self.todos = data;
                    self.$todoList.html( self.todoTemplate( self.todos ) );
                    self.$main.toggle( !!self.todos.length );
                    self.$toggleAll.prop( 'checked', !self.activeTodoCount() );
                    self.renderFooter();
                })
                .error(function() {
                    window.alert("Something bad happened");
                });
		},
		renderFooter: function() {
			var todoCount = this.todos.length,
				activeTodoCount = this.activeTodoCount(),
				footer = {
					activeTodoCount: activeTodoCount,
					activeTodoWord: Utils.pluralize( activeTodoCount, 'item' ),
					completedTodos: todoCount - activeTodoCount
				};

			this.$footer.toggle( !!todoCount );
			this.$footer.html( this.footerTemplate( footer ) );
		},
		toggleAll: function() {
			var isChecked = $( this ).prop('checked');
			$.each( App.todos, function( i, val ) {
				val.completed = isChecked;
			});
            Utils.updateAll(App.todos).success(function() {App.render()});
		},
		activeTodoCount: function() {
			var count = 0;
			$.each( this.todos, function( i, val ) {
				if ( !val.completed ) {
					count++;
				}
			});
			return count;
		},
		destroyCompleted: function() {
			var todos = App.todos,
				l = todos.length;
            var response;
			while ( l-- ) {
				if ( todos[l].completed ) {
                    response = Utils.delete(todos[l]);
				}
			}
            if (response) {
            response.success(function() { App.render() });
            } else {
                App.render();
            }
		},
		// Accepts an element from inside the ".item" div and
		// returns the corresponding todo in the todos array
		getTodo: function( elem, callback ) {
			var id = $( elem ).closest('li').data('id');
			$.each( this.todos, function( i, val ) {
				if ( val.id === id ) {
					callback.apply( App, arguments );
					return false;
				}
			});
		},
		create: function(e) {
			var $input = $(this),
				val = $.trim( $input.val() );
			if ( e.which !== App.ENTER_KEY || !val ) {
				return;
			}
            Utils.save({
                id: null,
                title: val,
                completed: false
            });
			$input.val('');
			App.render();
		},
		toggle: function() {
			App.getTodo( this, function( i, val ) {
				val.completed = !val.completed;
                Utils.save(val).success(function() { App.render() });
            });
		},
		edit: function() {
			$(this).closest('li').addClass('editing').find('.edit').focus();
		},
		blurOnEnter: function( e ) {
			if ( e.keyCode === App.ENTER_KEY ) {
				e.target.blur();
			}
		},
		update: function() {
			var val = $.trim( $(this).removeClass('editing').val() );
			App.getTodo( this, function( i ) {
				if ( val ) {
					this.todos[ i ].title = val;
				} else {
					this.todos.splice( i, 1 );
				}
				this.render();
			});
		},
		destroy: function() {
            var self = this;
            App.getTodo( this, function( i ) {
                Utils.delete(this.todos[i])
                    .error(function() {
                        window.alert('Something bad happened')
                    })
                    .complete(function() {
                        App.render()
                    }
                );
			});
		}
	};

	App.init();

});
