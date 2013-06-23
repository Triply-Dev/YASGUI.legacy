
function selectItemFromTab(tabId, link) {
	$('#' + tabId +' a[href="#' + link + '"]').tab('show');
}