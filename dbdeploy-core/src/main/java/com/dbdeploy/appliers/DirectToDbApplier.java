package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.scripts.ChangeScript;

import java.sql.SQLException;

public class DirectToDbApplier extends AbstractChangeScriptApplier {
	private final QueryExecuter queryExecuter;
	private final DatabaseSchemaVersionManager schemaVersionManager;
    private final QueryStatementSplitter splitter;

    public DirectToDbApplier(QueryExecuter queryExecuter, DatabaseSchemaVersionManager schemaVersionManager, QueryStatementSplitter splitter) {
		super(ApplyMode.DO);
		this.queryExecuter = queryExecuter;
		this.schemaVersionManager = schemaVersionManager;
        this.splitter = splitter;
    }

	public void begin() {
		try {
			queryExecuter.setAutoCommit(false);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void preChangeScriptApply(ChangeScript changeScript) {
		System.err.println("Applying " + changeScript + "...");
	}

	@Override
	protected void beginTransaction() {
		// no need to explictly begin, autoCommit mode has been disabled
	}

	@Override
	protected void applyChangeScriptContent(String scriptContent) {
		try {
            for (String statement : splitter.split(scriptContent)) {
                queryExecuter.execute(statement);
            }
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void insertToSchemaVersionTable(ChangeScript changeScript) {
		String sql = schemaVersionManager.getChangelogInsertSql(changeScript);
		try {
			queryExecuter.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void deleteFromSchemaVersionTable(ChangeScript changeScript) {
	}

	@Override
	protected void commitTransaction() {
		try {
			queryExecuter.commit();
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

	@Override
	protected void postChangeScriptApply(ChangeScript changeScript) {
	}


	public void end() {
	}
}
