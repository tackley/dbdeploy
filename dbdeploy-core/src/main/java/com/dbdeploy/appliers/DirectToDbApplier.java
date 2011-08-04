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
    private final boolean fake;
    private boolean quiet=false;

    public DirectToDbApplier(QueryExecuter queryExecuter, DatabaseSchemaVersionManager schemaVersionManager, QueryStatementSplitter splitter,
                             boolean fake) {
		this.queryExecuter = queryExecuter;
		this.schemaVersionManager = schemaVersionManager;
        this.splitter = splitter;
        this.fake = fake;
    }

    public void setQuiet(boolean quiet)
    {
        this.quiet = quiet;
    }

    public boolean getQuiet()
    {
        return this.quiet;
    }

    public void apply(List<ChangeScript> changeScript) {
        begin();

        String applyType = fake ? "Faking " : "Applying ";

        for (ChangeScript script : changeScript) {
            System.err.println(applyType + script + "...");

            if (! fake) {
                applyChangeScript(script);
            }
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
				if (statements.size() > 1 && !quiet) {
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
