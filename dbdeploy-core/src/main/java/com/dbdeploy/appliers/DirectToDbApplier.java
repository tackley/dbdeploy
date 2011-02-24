package com.dbdeploy.appliers;

import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.exceptions.ChangeScriptFailedException;
import com.dbdeploy.scripts.ChangeScript;

import java.sql.SQLException;
import java.util.List;

public class DirectToDbApplier implements ChangeScriptApplier {
	private final QueryExecuter queryExecuter;
	private final DatabaseSchemaVersionManager schemaVersionManager;
    private final QueryStatementSplitter splitter;

    public DirectToDbApplier(QueryExecuter queryExecuter, DatabaseSchemaVersionManager schemaVersionManager, QueryStatementSplitter splitter) {
		this.queryExecuter = queryExecuter;
		this.schemaVersionManager = schemaVersionManager;
        this.splitter = splitter;
    }

    public void apply(List<ChangeScript> changeScript) {
        begin();

        for (ChangeScript script : changeScript) {
            System.err.println("Applying " + script + "...");

            applyChangeScript(script);
            insertToSchemaVersionTable(script);

            commitTransaction();
        }
    }

	public void begin() {
		try {
			queryExecuter.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected void applyChangeScript(ChangeScript script) {
		List<String> statements = splitter.split(script.getContent());

		for (int i = 0; i < statements.size(); i++) {
			String statement = statements.get(i);
			try {
				if (statements.size() > 1) {
					System.err.println(" -> statement " + (i+1) + " of " + statements.size() + "...");
				}
				queryExecuter.execute(statement);
			} catch (SQLException e) {
				throw new ChangeScriptFailedException(e, script, i+1, statement);
			}
		}
	}

	protected void insertToSchemaVersionTable(ChangeScript changeScript) {
        schemaVersionManager.recordScriptApplied(changeScript);
	}

    protected void commitTransaction() {
		try {
			queryExecuter.commit();
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}


}
