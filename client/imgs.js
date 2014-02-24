var Imgs = function() {
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
		playRound : new Img("images/nounproject/play.png"),
		playSquare : new Img("images/nounproject/playLow.png"),
		playSquareError : new Img("images/nounproject/playSquareError.png"),
		crossThin : new Img("images/nounproject/crossThin.png"),
		loading: new Img("images/other/ajax_loader.gif"),
		addTab : new Img("images/nounproject/addPage.png"),
		editText: new Img("images/nounproject/editText.png"),
		copy: new Img("images/nounproject/copy.png"),
		crossRound: new Img("images/nounproject/close.png"),
		questionMark: new Img("images/nounproject/questionMark.png"),
		download: new Img("images/nounproject/download.png"),
		table: new Img("images/nounproject/table.png"),
		checkMark: new Img("images/nounproject/checkMark.png"),
		cross: new Img("images/nounproject/cross.png"),
		crossSmall: new Img("images/nounproject/crossSmall.png"),
		yasguiLogo: new Img("images/logos/yasgui.png")
		
	};
		
		
		
		
//		DISCONNECTED("nounproject/disconnected.png"),
//		OUTPUT_TABLE("outputFormats/table.png"),
//		OUTPUT_TABLE_SIMPLE("outputFormats/simpleTable.png"),
//		OUTPUT_RAW("outputFormats/rawResponse.png"),
//		CLOSE_TAB_SINGLE("other/close-one.png"),
//		CLOSE_TAB_OTHERS("other/close-others.png"),
//		CLOSE_TAB_ALL("other/close-all.png"),
//		ADD_TAB("nounproject/addPage.png"),
//		ADD("nounproject/add.png"),
//		BOOKMARK_QUERY("nounproject/bookmarkPage.png"),
//		SHOW_BOOKMARKS("nounproject/bookmarks.png"),
//		CHECKBOX("nounproject/checkbox.png"),
//		CHECKMARK("nounproject/checkMark.png"),
//		CHECK_CROSS("nounproject/checkCross.png"),
//		COPY_TAB("nounproject/copy.png"),
//		CROSS("nounproject/cross.png"),
//		DOWNLOAD("nounproject/download.png"),
//		DOWNLOAD_ROUND("nounproject/download_round.png"),
//		INFO("nounproject/info.png"),
//		LINK("nounproject/link.png"),
//		REFRESH("nounproject/refresh.png"),
//		SEARCH("nounproject/search.png"),
//		TABLE("nounproject/table.png"),
//		EDIT_TEXT("nounproject/editText.png"),
//		TEXT("nounproject/text.png"),
//		TOOLS("nounproject/tools.png"),
//		TOOLTIP("nounproject/tooltip.png"),
//		WARNING("nounproject/warning.png"),
//		LOADING("other/ajax_loader.gif"),
//		EXECUTE_QUERY("nounproject/playSquare.png"),
//		LOG_OUT("nounproject/logOut.png"),
//		LOG_IN("nounproject/logIn.png"),
//		QUESTION_MARK("nounproject/questionMark.png"),
//		COMPATIBLE("nounproject/compatible.png"),
//		EXTERNAL_LINK("nounproject/external_link.png"),
//		INTERNAL_LINK("nounproject/internal_link.png"),
//		BUG("nounproject/bug.png"),
//		
//		LOGO_GITHUB("logos/github.jpg"),
//		LOGO_GOOGLE("logos/google.png"),
//		LOGO_YAHOO("logos/yahoo.png"),
//		LOGO_OPENID("logos/openid.png"),
//		LOGO_DATA2SEMANTICS("logos/data2semantics.png"),
//		
//		OTHER_IMAGES_DIR("images/"),
//		OTHER_1PX("other/1px.png");
};
Yasgui.objs.Imgs = Imgs;

