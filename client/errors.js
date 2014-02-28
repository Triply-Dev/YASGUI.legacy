var Errors = function() {
	var id = "errorDialog";
	
	var getElement = function(errorMsg, title) {
		var el = $("#" + id);
		if (el.length == 0) {
			el = $( '<div id="' + id + '" title="' + title + '">' +
					errorMsg +
				'</div>' );
		} else {
			el.attr("title", title);
			el.html(errorMsg);
		}
		return el;
	};
	
	
	var drawError = function(errorMsg, title){
		if (!title) title = "Error";
		var el = getElement(errorMsg, title);
		el.dialog({
			closeOnEscape: true,
			height: 'auto'
		}).dialog("open");
		//add event handler on -complete- error obj (i.e. use class name)
		dismissOnOutsideClick(".ui-dialog-titlebar", function(){$("#" + id).dialog("close");});
	};
	
	return {
		draw: drawError
	};
};
Yasgui.errors = new Errors();

