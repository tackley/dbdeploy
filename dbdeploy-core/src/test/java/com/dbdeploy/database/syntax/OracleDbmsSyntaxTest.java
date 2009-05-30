package com.dbdeploy.database.syntax;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class OracleDbmsSyntaxTest  {

    @Test
    public void shouldComplyWithImplicitTransactionsForOracle() {
        DbmsSyntax oracleSyntax = DbmsSyntax.createFor("ora");
        assertEquals("", oracleSyntax.generateBeginTransaction());
    }

}
