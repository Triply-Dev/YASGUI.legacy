//Files in the lib dir are loaded before all the common yasgui js files (see http://doc.meteor.com)


//avoid 'undefined' errors in internet explorer
if ( ! window.console ) console = { log: function(){} };