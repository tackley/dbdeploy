package com.dbdeploy.database.syntax;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class OracleSqlPlusDbmsSyntaxTest{

    @Test
    public void shouldComplyWithImplicitTransactionsForOracle(){
        DbmsSyntax oracleSqlPlus = DbmsSyntax.createFor("ora-sqlplus");
        assertEquals("", oracleSqlPlus.generateBeginTransaction());
    }
}
