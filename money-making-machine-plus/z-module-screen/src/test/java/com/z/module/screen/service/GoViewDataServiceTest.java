
package com.z.module.screen.service;

import com.z.module.screen.web.vo.GoViewDataRespVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GoViewDataServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SqlRowSet sqlRowSet;

    @Mock
    private SqlRowSetMetaData sqlRowSetMetaData;

    @InjectMocks
    private GoViewDataService goViewDataService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDataBySQL() {
        String sql = "SELECT * FROM test";
        String[] columnNames = {"id", "name"};

        when(jdbcTemplate.queryForRowSet(sql)).thenReturn(sqlRowSet);
        when(sqlRowSet.getMetaData()).thenReturn(sqlRowSetMetaData);
        when(sqlRowSetMetaData.getColumnNames()).thenReturn(columnNames);

        when(sqlRowSet.next()).thenReturn(true).thenReturn(false);
        when(sqlRowSet.getObject("id")).thenReturn(1);
        when(sqlRowSet.getObject("name")).thenReturn("test_name");

        GoViewDataRespVO result = goViewDataService.getDataBySQL(sql);

        assertEquals(2, result.getDimensions().size());
        assertEquals(1, result.getSource().size());
        assertEquals(1, result.getSource().get(0).get("id"));
        assertEquals("test_name", result.getSource().get(0).get("name"));
    }
}
