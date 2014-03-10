(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
		
	/**
	 * 
	 * @param options: {
	 * 	id
	 * 	title
	 * 	content
	 * 	width
	 * 	height
	 * 	position
	 *  onClose
	 * 
	 * }
	 */
	this.Yasgui.widgets.dialog = function(options) {
		options = options || {};
		var id = options.id || "dialog";
		var title = options.title || "&nbsp";
		var content = options.content || "";
		
		var getElement = function() {
			var el = $("#" + id);
			if (el.length == 0) {
				el = $( '<div id="' + id + '" title="' + title + '"></div>' );
				el.append(content);
			} else {
				el.attr("title", title);
				el.html(content);
			}
			return el;
		};
		
		
		var draw = function(){
			console.log(config);
			var el = getElement();
			el.dialog({
				closeOnEscape: true,
//				height: 'auto',
				height: options.height || 'auto',
				width: options.width || 'auto',
				position: options.position || 'center'
			}).dialog("open");
			//add event handler on -complete- error dialog (i.e. use class name)
			dismissOnOutsideClick(".ui-dialog", function() {
				if (options.onClose) options.onClose();
				$("#" + id).remove();
			});
		};
		
		
		draw();
	};
}).call(this);

