package org.onap.clamp.clds.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.onap.clamp.clds.model.CldsServiceData;
import org.onap.clamp.clds.util.MockResultSet;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CldsServiceDataMapperTest {

    @Mock
    private CldsServiceDataMapper cldsServiceDataMapper;

    @Mock
    private CldsServiceData cldsServiceData;


    private ResultSet getResultSet() throws SQLException{
        ResultSet resultSet = MockResultSet.create(
                new String[] { "serviceInvariantUuid", "serviceUiod","ageOfRecord","cldsVfs","XY" }, //columns
                new Object[][] {
                        { "XYZ", "ZYX", 33L,"List",90}
                });
        return resultSet;
    }


    @Test
    public void shoduldRetrunCldsServiceDsataObj() throws SQLException {

        byte[] bytes = "A byte array".getBytes();
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);//!!!!!!!!1???

       ResultSet resultSet = MockResultSet.create(
                new String[] { "serviceInvariantUuid", "serviceUiod","ageOfRecord","cldsVfs","XY" }, //columns
                new Object[][] {
                        { "XYZ", "ZYX", 33L,blob,90}
                });
CldsServiceDataMapper cldsServiceDataMapper = new CldsServiceDataMapper();

        while (resultSet.next()) {
            resultSet.getString(1);
           resultSet.getString(2);
        }

        resultSet.getBlob(3).getBinaryStream();
        CldsServiceData cldsServiceDataResult =  cldsServiceDataMapper.mapRow(resultSet,550);
      //  Assert.assertEquals(cldsServiceData,cldsServiceDataResult);
    }


}
