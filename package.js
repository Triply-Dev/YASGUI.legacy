Package.describe({
  summary: "Package collection of external libraries used by YASGUI"
});


Package.on_use(function(api, where) {
        var where = 'client';

		//Codemirror
        api.add_files('lib/codemirror/lib/codemirror.js', where);
        api.add_files('lib/codemirror/mode/javascript/javascript.js', where);
        api.add_files('lib/codemirror/mode/xml/xml.js', where);
        api.add_files('lib/codemirror/mode/turtle/turtle.js', where);
        api.add_files('lib/codemirror/addon/hint/show-hint.js', where);
        api.add_files('lib/codemirror/addon/search/searchcursor.js', where);
        api.add_files('lib/codemirror/addon/edit/matchbrackets.js', where);
        api.add_files('lib/codemirror/addon/runmode/runmode.js', where);
//        api.add_files('lib/codemirror/util/formatting.js', where);
//formatting not in v3.0...
        api.add_files('lib/codemirror/lib/codemirror.css', where);
        api.add_files('lib/codemirror/addon/hint/show-hint.css', where);
        
        //jquery ui
	//IMPORTANT: when adding a new jquery ui version, make sure the image paths in the css file are changed
        api.add_files('lib/jquery-ui-1.10.3.custom/css/bootstrap/jquery-ui-1.10.0.custom.css', where);
        api.add_files('lib/jquery-ui-1.10.3.custom/css/bootstrap/jquery.ui.1.10.0.ie.css', where);
        api.add_files('lib/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.js', where);
        
});
