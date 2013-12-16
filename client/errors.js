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
	
//	var dismissOnOutsideClick = function() {
//		$(document).on("click." + id, function(event) {
//		    if(!$(event.target).parents().andSelf().is("#" + id)) {
//		    		$("#" + id).dialog("close");
//		    		//remove this listener to avoid garbage
//		    		$(document).off("click." + id);
//			}
//		});
//	};
	
	var drawError = function(errorMsg, title){
		if (!title) title = "Error";
		var el = getElement(errorMsg, title);
		el.dialog({
			closeOnEscape: true,
			height: 'auto'
		}).dialog("open");
		dismissOnOutsideClick(id, function(){$("#" + id).dialog("close");});
	};
	
	return {
		draw: drawError,
	};
};
Yasgui.errors = new Errors();

