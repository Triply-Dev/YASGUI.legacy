package com.data2semantics.yasgui.client.tab;  
  
import com.smartgwt.client.data.DataSource;  
import com.smartgwt.client.data.fields.*;  
import com.smartgwt.client.types.DSDataFormat;
  
public class EndpointsDataSource extends DataSource {  
  
    private static EndpointsDataSource instance = null;  
  
    public static EndpointsDataSource getInstance() {  
        if (instance == null) {  
            instance = new EndpointsDataSource("endpointsDataSource");  
        }  
        return instance;  
    }  
  
    public EndpointsDataSource(String id) {  
        setID(id);  
        setDataFormat(DSDataFormat.JSON);
        DataSourceTextField endpointUriField = new DataSourceTextField("endpointUri", "Endpoint", 256, true);  
//        pkField.setHidden(true);  
        endpointUriField.setPrimaryKey(true);  
  
        DataSourceTextField datasetUriField = new DataSourceTextField("datasetUri", "Dataset Uri", 256, true);  
        datasetUriField.setHidden(true);
        DataSourceTextField datasetTitleField = new DataSourceTextField("datasetTitle", "Dataset Title", 128, true);  
  
        DataSourceTextField descriptionField = new DataSourceTextField("description", "Description", 2000);  
  
        setFields(endpointUriField, datasetUriField, datasetTitleField, descriptionField);  
  
        setDataURL("cache/test.json");  
        setClientOnly(true);          
    }  
}  