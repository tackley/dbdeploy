package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
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

    public void apply(List<ChangeScript> changeScript, ApplyMode applyMode) {
        begin();

        for (ChangeScript script : changeScript) {
            System.err.println("Applying " + script + "...");

            if (applyMode == ApplyMode.DO)
                applyChangeScriptContent(script.getContent());
            else if (applyMode == ApplyMode.UNDO)
                applyChangeScriptContent(script.getUndoContent());
            
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

	protected void applyChangeScriptContent(String scriptContent) {
		try {
            for (String statement : splitter.split(scriptContent)) {
                queryExecuter.execute(statement);
            }
		} catch (SQLException e) {
			throw new RuntimeException(e);
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
