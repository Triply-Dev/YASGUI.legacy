
$(function() {
	
	
	var tabCounter = 1,
		tabSetSelector = "#tabset",
		tabs = null;
	
	var init = function() {
		
		Yasgui.tabs = {
			getCurrentTab: function() {
				//helper function for getting selected tab content
				return Yasgui.tabs[Yasgui.settings.getSelectedTab().id];
			},
			getAllTabIds: function() {
				var keys = [];
				for(var k in Yasgui.tabs) {
					if (k.startsWith("tabs-")) keys.push(k);
				}
				return keys;
			},
			positionElements : function() {
				var tabIds = Yasgui.tabs.getAllTabIds();
				for (var tabIdKey = 0; tabIdKey < tabIds.length; tabIdKey++) {
					var tabId = tabIds[tabIdKey];
					if (Yasgui.tabs[tabId].positionElements) Yasgui.tabs[tabId].positionElements();
				}
			}
		};
		
		tabs = $(tabSetSelector).tabs({
			activate: function(event, ui) {
				//a tab is selected
				var selectedTabId = ui.newTab.find("a").first().attr('href');
				if (selectedTabId != undefined) {
					selectedTabId = selectedTabId.substring(1); //remove #!
					var tabKey = getTabKeyFromId(selectedTabId);
					if (tabKey != null) {
						Yasgui.settings.selectedTabKey = getTabKeyFromId(selectedTabId);
						Yasgui.settings.store();
					} else {
						console.log("hmm, no tab key found?");
					}
					
					//make sure we draw the tab content if it isnt there yet..
					if ($("#" + selectedTabId).html().length == 0) {
						//content is not drawn yet. Draw it!
						if (Yasgui.tabs[selectedTabId] == undefined) {
							Yasgui.tabs[selectedTabId] = new Yasgui.objs.TabContent(Yasgui.settings.getSelectedTab());
						}
					}
				} else {
					console.log("could not detect current selected id based on DOM");
				}
				
				return false;
			}
		});
		$(tabSetSelector).
			delegate("li.ui-state-active a", "dblclick",
				function() {
					
					renameTab($(this).attr("href").substring(1));
				}).
			delegate("li a", "contextmenu", function(event) {
				event.preventDefault();
				drawTabContextMenu(event);
				return false;
			});
		
		$("#tabContextMenu").hide().zIndex(Yasgui.constants.zIndexes.popupDialogues).width(200).menu();
		

		
		// make them sortable
		tabs.find(".ui-tabs-nav").sortable(
				{
					delay: 150, //prevent unwanted drag event when just clicking on a tab
					axis : "x",
					stop : function(event, ui) {
						var lastElClassname = $(($(this).find("li:last").get(0)))
								.attr("class");
						if (lastElClassname == undefined
								|| lastElClassname != "addTabItm") {
							// we are trying to move a tab behind the 'add tab'
							// button! We say NO to that!
							$(this).sortable("cancel");
						}
						//we should update the settings now
						updateSortedTabsInSettings();
						tabs.tabs("refresh");
					}
				});
		$('#addTab').on('click', function() {
			var tabSettings = addTab(Yasgui.settings.defaultTabSettings, true);
			selectTabFromId(tabSettings.id);
		});
		
		// close icon: removing the tab on click
		tabs.delegate("span.closeTab", "click", function(event) {
			event.stopPropagation();
			closeTab($(this).closest("a").attr("href").substring(1));
		});
		
		
	};
	
	var closeTab = function(panelId, store) {
		$('a[href="#' + panelId + '"]').closest("li").remove().attr("aria-controls");
		//remove content from dom
		$("#" + panelId).remove();
		//remove from settings
		Yasgui.settings.tabs.splice(getTabKeyFromId(panelId), 1);
		tabs.tabs("refresh");
		

		// remove our javascript tab object
		Yasgui.tabs[panelId] = undefined;
		try {
			delete Yasgui.tabs[panelId];
		} catch (e) {
		}
		
		//recheck our selected tab
		Yasgui.settings.selectedTabKey = getSelectedTabFromDom();
		if (store) Yasgui.settings.store();
	};
	var closeOtherTabs = function(tabId) {
		$("#tabs a").each(function() {
			var currentTabId = $(this).attr("href").substring(1);
			if (currentTabId != tabId) {
				closeTab($(this).attr("href").substring(1));
			}
		});
		Yasgui.settings.store();
	};
	
	var closeAllTabs = function() {
		$("#tabs a").each(function() {
			closeTab($(this).attr("href").substring(1));
		});
		Yasgui.settings.store();
	};
		
	var drawTabContextMenu = function(event) {
		var menuSelector = "tabContextMenu";
		var tabId = $(event.currentTarget).closest("a").attr("href").substring(1);//remove #
		//set click handlers
		$("#renameTab").click(function(event){
			event.stopPropagation(); //otherwise, the rename tab click handler is fired
			$("#" + menuSelector).hide();
			renameTab(tabId);
		});
		$("#closeTab").click(function() {
			closeTab(tabId, true);
			$("#" + menuSelector).hide();
		});
		$("#closeOtherTabs").click(function() {
			closeOtherTabs(tabId, true);
			$("#" + menuSelector).hide();
		});
		$("#closeAllTabs").click(function() {
			closeAllTabs(tabId, true);
			$("#" + menuSelector).hide();
		});
		
		
		//Strange things going on here. First, we need to reset the position, as otherwise, the next time we summon 
		//the menu, it gets drawn in the wrong location (2x the x and y of the previous location..)
		$("#" + menuSelector).css({left:"0px", top:"0px"});
		
		//now, we need to show it TWICE! Otherwise, it would take two right-clicks to summon our menu..
		$("#" + menuSelector).position({
			my : "left top",
			of : event
		}).show();
		$("#" + menuSelector).position({
			my : "left top",
			of : event
		}).show();
		dismissOnOutsideClick(menuSelector, function(){$("#" + menuSelector).hide();});
	};
	
	var updateSortedTabsInSettings = function() {
		var tabIds = [], newTabSettingsArray = [];
		$(tabSetSelector + " a").each(function() {
			tabIds.push($(this).attr('href').substring(1)); //remove final #
		});
		var tabSettingsArray = Yasgui.settings.tabs;
		
		for (var i = 0; i < tabIds.length; i++) {
			var tabId = tabIds[i];
			var tabSettings = $.grep(tabSettingsArray, function(e){ return e.id == tabId; });
			if (tabSettings.length != 1) {
				console.log("trying to update sorted elements in settings, but something went wrong. Expected 1 result for id " + tabId + ", but got " + tabSettings.length + " results");
				return;
			} else {
				newTabSettingsArray.push(tabSettings[0]);
			}
		}
		Yasgui.settings.tabs = newTabSettingsArray;
		Yasgui.settings.store();
	};
	
	

	var getValidNewId = function(counter) {
		if (counter == undefined) counter = tabCounter;
		var id = "tabs-" + counter;
		if (tabIdExists(id)) {
			return getValidNewId(counter + 1);
		} else {
			return id;
		}
	};
	
	var tabIdExists = function(tabId) {
		return (document.getElementById(tabId) != undefined);
	};
	
	var getCloseButton = function() {
		return "<span class='closeTab'>&nbsp;</span>";
	};
	
	var getNewTabTitle = function(defaultTabTitle) {
		var tabTitleExists = function(titleToCheck) {
			var exists = false;
			for (var i = 0; i < Yasgui.settings.tabs.length; i++) {
				if (Yasgui.settings.tabs[i].tabTitle == titleToCheck) {
					exists = true;
					break;
				}
			}
			return exists;
		};
		var newTitle = null;
		if (!tabTitleExists(defaultTabTitle)) {
			newTitle = defaultTabTitle;
		} else {
			//only try for a limited period
			for (var i = 1; i < 100; i++) {
				var testTitle = defaultTabTitle + "-" + i;
				if (!tabTitleExists(testTitle)) {
					newTitle = testTitle;
					break;
				}
			}
		}
		return (newTitle? newTitle: defaultTabTitle);
	};
	var addTab = function(tabSettings, newlyCreatedTab) {
		var tabSettings = jQuery.extend(true, {}, tabSettings);
		var id;
		if (tabSettings.id == undefined || tabIdExists(tabSettings.id)) {
			id = getValidNewId();
			tabSettings.id = id;
		} else {
			id = tabSettings.id;
		}
		
		
		var tabTitle = (newlyCreatedTab? getNewTabTitle(tabSettings.tabTitle): tabSettings.tabTitle),
				li = $(tabTemplate = "<li style='vertical-align:middle;'><a href='#" + id + "'>" + tabTitle + getCloseButton() + "</a></li>");
		tabSettings.id = id;
		tabSettings.tabTitle = tabTitle;
		tabs.find(".ui-tabs-nav").append(li);
		var tabContent = $("<div id='" + id + "'></div>");
		tabs.append(tabContent);;
		tabs.tabs("refresh");
		tabCounter++;
		
		//make sure our 'add tab' button is last!
		$(".addTabItm").clone(true).appendTo("#tabs");
		$(".addTabItm:first").remove();
		if (newlyCreatedTab) {
			Yasgui.settings.tabs.push(tabSettings);
			Yasgui.settings.store();
		}
		return tabSettings;
	};


	function renameTab(tabId) {
		var  obj = $('a[href="#' + tabId + '"]'),
			oldName = obj.text(), 
			editMode = '<div class="editable"><form id="rename_tab_form"><input type="text" style="width: 120px;"id="new_tab_name" value="'
				+ oldName
				+ '" name="new_tab_name" maxlength="20" /></form></div>';
		//remove the close button during editing
		obj.next().remove();
		// Inject the form after the span, and then remove the span from DOM
		obj.after(editMode).remove();
		$("div.editable", "#tabs-nav").closest("a").addClass("editing");
		$("#new_tab_name").on("focus", function() {
			this.select();
		}).focus().keydown(function(e) {
			if (37 <= e.keyCode && e.keyCode <= 40) {
				// this is an arrow key
				// stop propagation, as we don't want to switch tabs!
				e.stopPropagation();
			} else if (e.keyCode == 13) {
				// enter
				replaceName(tabId, $("#new_tab_name"), $("#new_tab_name").val());
			} else if (e.keyCode == 27) {
				// escape
				replaceName(tabId, $("#new_tab_name"), oldName);
			}
		});
		$("body").on("click.tabnamedit", function(e) {
			var target = e.target.id;
			if (target != 'new_tab_name') {
				replaceName(tabId, $("#new_tab_name"), $("#new_tab_name").val());
				$("body").off("click.tabnamedit");
			}
		});
	}
	function replaceName(tabId, editObj, newVal) {
		if (newVal == '' || newVal == undefined) {
			newVal = 'Untitled';
		}
		Yasgui.settings.getSelectedTab().tabTitle = newVal;
		Yasgui.settings.store();
		// remove current title
		$(editObj).parents("div.editable").after('<a href="#' + tabId + '">' + newVal +  getCloseButton() + '</a> ').remove();
		// remove editing part
		$("li.ui-state-active a", "#tabs-nav").closest("a").removeClass(
				"editing");
		$("body").off("click.tabnamedit");
	}
	
	var getSelectedTabFromDom = function() {
		return $('#tabs .ui-tabs-active').index();
	};
	var selectTabFromId = function(tabId) {
		var tabIndex = $('#tabs a[href="#' + tabId + '"').parent().index();
		$(tabSetSelector).tabs("option", "active", tabIndex);
	};
	
	var selectTabFromSettings = function() {
		var selectedTabSettings = Yasgui.settings.getSelectedTab();
		var tabId = null;
		if (selectedTabSettings == null || !tabIdExists(selectedTabSettings.id)) {
			//select first tab item
			if (Yasgui.settings.tabs.length > 0 && Yasgui.settings.tabs[0].id != undefined) {
				tabId = Yasgui.settings.tabs[0].id;
			}
			
			if (tabId == undefined) {
				//hmm, use dom to detect first tab id!
				tabId = $("#tabs a" ).first().attr('href').substring(1); //remove the #!
			}
			//don't need to store. this is done by 'selected' event hook
		} else {
			tabId = selectedTabSettings.id;
		}
		
		selectTabFromId(tabId);
		
	};
	var getTabKeyFromId = function(id) {
		for (var key = 0; key < Yasgui.settings.tabs.length; key++) {
			if (Yasgui.settings.tabs[key].id == id) {
				return key;
			}
		}
		return null;
	};
	
	var addTabsFromSettings = function() {
		for (var i = 0; i < Yasgui.settings.tabs.length; i++) {
			addTab(Yasgui.settings.tabs[i]);
		}
		selectTabFromSettings();
	};
	
	init();
	addTabsFromSettings();
});