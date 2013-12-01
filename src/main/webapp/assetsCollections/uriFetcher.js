var shouldWeFetchNotification = {
	id: "shouldFetchNoty",
	content: "YASGUI just tried to <a href='http://laurensrietveld.nl/yasgui/help.html#autocompletionmethods' target='_blank'>extract</a> properties and classes from your query, allowing you to autocomplete such URIs.<br>" +
			"However, the YASGUI server was unsuccessful in reaching your endpoint. This either means the endpoint is installed on your local computer, or running in an intranet.<br>" +
			"To support autocompletions for localhost endpoints, log in to YASGUI and configure localhost autocompletions.",
	draw: function() {
		noty({
			text: this.content,
			layout: 'bottomLeft',
			type: 'alert',
			id: this.id,
			closeWith: ["button"],
			buttons: [
			          {text: 'Login and configure autocompletions', onClick: function($noty) {
			              $noty.close();
			              setUriFetcherNotificationShown();
			              addToLoginStack("showAutocompletionConfig();");
			              login();
			            },
			          },
			          {text: 'Ask me later', onClick: function($noty) {
			              $noty.close();
			            }
			          },
			          {text: 'Don\'t ask me again', onClick: function($noty) {
			              $noty.close();
			              setUriFetcherNotificationShown();
			            }
			          }
			        ]
		});
	},
	
};
var loginAndConfigureAutocompletions = function() {
	//first set callback for logging in
	
};


