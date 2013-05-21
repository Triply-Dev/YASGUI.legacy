package com.data2semantics.yasgui.client.tab.optionbar.bookmarks;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;

public class BookmarksDataSource extends DataSource {

	public BookmarksDataSource() {
		setID(id);
		setRecordXPath("/List/country");
		DataSourceField countryNameField = new DataSourceField("countryName", FieldType.TEXT, "Country");
		DataSourceField countryCodeField = new DataSourceField("countryCode", FieldType.TEXT, "Code");
		DataSourceField independenceField = new DataSourceField("independence", FieldType.DATE, "Independence");
		DataSourceField populationField = new DataSourceField("population", FieldType.INTEGER, "Population");
		DataSourceField gdpField = new DataSourceField("gdp", FieldType.FLOAT, "GDP ($B)");
		setFields(countryNameField, countryCodeField, independenceField, populationField, gdpField);
		setDataURL("ds/test_data/country.data.xml");
	}
}
