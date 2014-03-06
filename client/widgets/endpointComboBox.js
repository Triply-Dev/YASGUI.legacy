(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.EndpointComboBox = function(parent, tabSettings) {

//		wrapper.append("<label class='topLabel'>Select an endpoint</label>" +
//				"<select class='endpointSelector'>" +
//				"<option value='bla'>bla</option>" +
//				"<option value='dbpedia'>dbpedia</option>" +
//				"</select>");
		    $.widget( "custom.combobox", {
//		    	options: {
//		            endpoints: ["http://dbpedia.org", "http://dbpediasdd.org"],
//		          },
		          // _setOptions is called with a hash of all options that are changing
		          // always refresh when changing options
//		          _setOptions: function() {
//		            // _super and _superApply handle keeping the right this-context
//		            this._superApply( arguments );
//		            this._refresh();
//		          },
		     
		          // _setOption is called for each individual option that is changing
		          _setOption: function( key, value ) {
		            // prevent invalid color values
		            this._super( key, value );
		          },
		      _create: function() {
		        this.wrapper = $( "<span>" )
		          .addClass( "custom-combobox" )
		          .insertAfter( this.element );
		 
		        this.element.hide();
		        this._createAutocomplete();
		        this._createShowAllButton();
		      },
		 
		      _createAutocomplete: function() {
		    	  //need to wrap input in div, as in firefox we cannot 'stretch' the input by defining top and bottom offsets
		    	  var inputWrapper = $("<div class='comboBoxTextWrapper'></div>").appendTo(this.wrapper);
		        this.input = $( "<input>" )
		          .appendTo( inputWrapper )
		          .val( this.options.selectedEndpoint )
		          .attr( "title", "" )
		          .addClass( "custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left" );
		        var main = this;
		        this.input.autocomplete({
		            delay: 20,
		            minLength: 0,
		            source: $.proxy( this, "_source" )
		          }).autocomplete("widget")
		          	.delegate(".comboDeleteEndpointIcon", "click", function(event){
//		          		console.log($(event.target).sibblings(".endpointComboValue"));
		          		Yasgui.endpoints.deleteEndpoint($(event.target).parent().siblings(".endpointComboValue").text());
		          		event.stopPropagation();
		          		main.input.val("");
		          		main.input.autocomplete("search", "asdfds");
//		          		$(event.target).closest(".custom-combobox-input").val("dd");
//		          		this.input.val = "";
//		          		input.autocomplete( "search" ,"");
		          	});
		        this.input.data( "ui-autocomplete" )._renderItem = function( ul, item ) {
//			        	console.log(ul);
//			        	console.log(item);
		        	var html = "<a " + (item.desc?"title='" + item.desc + "'" : "") + ">" +
            		"<span class='endpointComboLabel'>" + (item.label?item.label:"&nbsp") + "</span>" +
    				"<span class='endpointComboValue'>" + item.value + "</span>" +
    				"<span class='endpointComboDelete'>" + 
    					(item.isCustomEndpoint? "<img class='comboDeleteEndpointIcon' title='Dont suggest this endpoint anymore' src='" + Yasgui.constants.imgs.crossRound.get() + "'>": "&nbsp;") +
    				"</span>" + 
    				"<span class='endpointComboDesc'>" + 
    					(item.descLink != undefined? "<img class='comboOpenEndpointDesc' title='show additional endpoint information' src='" + Yasgui.constants.imgs.questionMark.get() + "'>": "&nbsp;") + 
					"</span>" +
					"</a>" ;
		        	
		        	
				     	return $( "<li>" )
				            .append( html )
				            .appendTo( ul );
				        };
//		        this.input.autocomplete().__renderItem = function( ul, item ) {
//		        	console.log(ul);
//		        	console.log(item);
////			          return $( "<li>" )
////			            .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
////			            .appendTo( ul );
//			          return $( "<li>" ).append("<a>blaaaat</a>");
//			        };
//		        .data( "ui-autocomplete" )._renderItem = function( ul, item ) {
//		        	console.log(ul);
//		        	console.log(item);
////			          return $( "<li>" )
////			            .append( "<a>" + item.label + "<br>" + item.desc + "</a>" )
////			            .appendTo( ul );
//			          return $( "<li>" ).append("<a>blaaaat</a>");
//			        };
//		          .tooltip({
//		            tooltipClass: "ui-state-highlight"
//		          });
		 
		        this._on( this.input, {
		          autocompleteselect: function( event, ui ) {
//		        	  tabSettings.endpoint = ui.item.value;
//		        	  Yasgui.settings.store();
		        	  
		          },
		          blur: function(){
		        	  tabSettings.endpoint = this.input.val();
		        	  Yasgui.settings.store();
		        	  Yasgui.endpoints.addEndpointIfNeeded(this.input.val());
		        	  Yasgui.sparql.checkCorsEnabled(this.input.val());
//		        	  tabSettings.endpoint = ui.item.value;
//		        	  Yasgui.settings.store();
		          }
		        });
		      },
		 
		      _createShowAllButton: function() {
		        var input = this.input,
		          wasOpen = false;
		 
		        $( "<a>" )
		          .attr( "tabIndex", -1 )
		          .attr( "title", "Show All Items" )
//		          .tooltip()
		          .appendTo( this.wrapper )
		          .button({
		            icons: {
		              primary: "ui-icon-triangle-1-s"
		            },
		            text: false
		          })
		          .removeClass( "ui-corner-all" )
		          .addClass( "custom-combobox-toggle ui-corner-right" )
		          .mousedown(function() {
		        	  
		            wasOpen = input.autocomplete( "widget" ).is( ":visible" );
		          })
		          .click(function() {
		            input.focus();
		 
		            // Close if already visible
		            if ( wasOpen ) {
		              return;
		            }
		 
		            input.autocomplete( "search" );
		          });
		      },
		      _source: function( request, response ) {
		        var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
		        
		        response($.grep(this.options.endpoints, function (value) {
		            return matcher.test(value.value);
		        }));
		        
		      },
		 
		      _destroy: function() {
		        this.wrapper.remove();
		        this.element.show();
		      }
		    });
//		  })( jQuery );
		  $(function() {
				var fakeInput = $("<div></div>");
				
				
				parent.append(fakeInput);
				fakeInput.combobox({endpoints: Yasgui.endpoints.endpoints, selectedEndpoint: tabSettings.endpoint});
				Yasgui.sparql.checkCorsEnabled(tabSettings.endpoint);
		  });
		
		
//		<div id="project-label">Select a project (type "j" for a start):</div>
//		<img id="project-icon" src="images/transparent_1x1.png" class="ui-state-default" alt="">
//		<input id="project">
	};
}).call(this);
