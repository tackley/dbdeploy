package com.dbdeploy.database;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: jesuspg
 * Date: 5/24/12
 * Time: 11:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryStatementSplitterExtTest {



    @Test
    public void shouldSupportTwoDelimiters() {

        //Given

        QueryStatementSplitterExt splitterExt=new QueryStatementSplitterExt(";",DelimiterType.normal);

        splitterExt.addDelimiter("SQL",new Delimiter(";",DelimiterType.normal));
        splitterExt.addDelimiter("PLSQL",new Delimiter("/",DelimiterType.row));
        final String expectedItem1="SELECT 1";
        final String expectedItem2="CREATE PROCEDURE\\n select 1;\\nEND;\n";
        final String inputWithSQLandPLSQL=("--//#SQL\n"+expectedItem1+";\n--//#PLSQL\n"+expectedItem2+"/");

        //When

        List<String> statements= splitterExt.split(inputWithSQLandPLSQL)  ;

        //Then
        assertThat(statements.size(),is(2));
        assertThat(statements ,hasItems(expectedItem1,expectedItem2));


    }





}
