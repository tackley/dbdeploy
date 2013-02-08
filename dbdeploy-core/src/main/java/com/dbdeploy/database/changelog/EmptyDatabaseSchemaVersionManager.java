package com.dbdeploy.database.changelog;

import com.dbdeploy.exceptions.SchemaVersionTrackingException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper around {@link DatabaseSchemaVersionManager} for when a changelog table is not present
 */
public class EmptyDatabaseSchemaVersionManager extends DatabaseSchemaVersionManager {

    public EmptyDatabaseSchemaVersionManager(QueryExecuter queryExecuter, String changeLogTableName) {
        super(queryExecuter, changeLogTableName);
    }

    private boolean changelogTableExists() {
        try {
            return queryExecuter.doesTableExist(changeLogTableName);
        } catch (SQLException e) {
            throw new SchemaVersionTrackingException("Could not determine presence of change log table", e);
        }
    }

    public List<Long> getAppliedChanges() {
        if (!changelogTableExists()) {
            return Collections.emptyList();
        } else {
            return super.getAppliedChanges();
        }
    }
}
