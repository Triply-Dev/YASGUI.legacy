var Imgs = function() {
	var paths = {
		playRound : "images/nounproject/play.png",
		playSquare : "images/nounproject/playLow.png",
		playSquareError : "images/nounproject/playSquareError.png",
		crossThin : "images/nounproject/crossThin.png",
		loading: "images/other/ajax_loader.gif",
		addTab : "images/nounproject/addPage.png",
		editText: "images/nounproject/editText.png",
		copy: "images/nounproject/copy.png",
		crossRound: "images/nounproject/close.png",
		questionMark: "images/nounproject/questionMark.png",
		download: "images/nounproject/download.png",
		table: "images/nounproject/table.png",
		checkMark: "images/nounproject/checkMark.png",
		cross: "images/nounproject/cross.png",
		
		
		
		
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
	var getDisabled = function(key) {
		return paths[key].replace(/(\.[^\.]+)$/, '_disabled$1');;
	};
	var getBold = function(key) {
		return paths[key].replace(/(\.[^\.]+)$/, '_bold$1');;
	};
	paths.getDisabled = getDisabled;
	paths.getBold = getBold;
	return paths;
};
Yasgui.objs.Imgs = Imgs;

