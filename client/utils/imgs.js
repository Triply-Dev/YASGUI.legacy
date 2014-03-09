(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.objs = this.Yasgui.objs || {};
	this.Yasgui.objs.Imgs = function() {
		Img = function(path) {
			return {
				getDisabled: function() {
					return path.replace(/(\.[^\.]+)$/, '_disabled$1');;
				},
				getBold: function() {
					return path.replace(/(\.[^\.]+)$/, '_bold$1');;
				},
				get: function(){
					return path;
				}
			};
		};
		
		return  {
			play : new Img("images/nounproject/play.png"),
			crossThin : new Img("images/nounproject/crossThin.png"),
			loading: new Img("images/other/ajax_loader.gif"),
			editText: new Img("images/nounproject/editText.png"),
			copy: new Img("images/nounproject/copy.png"),
			crossRound: new Img("images/nounproject/close.png"),
			questionMark: new Img("images/nounproject/questionMark.png"),
			download: new Img("images/nounproject/download.png"),
			table: new Img("images/nounproject/table.png"),
			checkMark: new Img("images/nounproject/checkMark.png"),
			cross: new Img("images/nounproject/cross.png"),
			crossSmall: new Img("images/nounproject/crossSmall.png"),
			plus: new Img("images/nounproject/plus.png"),
			externalLink: new Img("images/nounproject/externalLink.png"),
			gear: new Img("images/nounproject/gearLarge.png"),
			yasguiLogo: new Img("images/logos/yasgui.png")
			
		};
	};
}).call(this);

