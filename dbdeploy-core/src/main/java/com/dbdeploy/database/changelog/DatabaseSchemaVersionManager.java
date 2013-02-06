package com.dbdeploy.database.changelog;

import com.dbdeploy.AppliedChangesProvider;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.scripts.ChangeScript;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * This class is responsible for all interaction with the changelog table
 */
public class DatabaseSchemaVersionManager implements AppliedChangesProvider {

    private final QueryExecuter queryExecuter;
    private final String changeLogTableName;
    private CurrentTimeProvider timeProvider = new CurrentTimeProvider();

    public DatabaseSchemaVersionManager(QueryExecuter queryExecuter, String changeLogTableName) {
        this.queryExecuter = queryExecuter;
        this.changeLogTableName = changeLogTableName;
    }

    private boolean changelogTableExists() {
        try {
            return queryExecuter.doesTableExist(changeLogTableName);
        } catch (SQLException e) {
            throw new SchemaVersionTrackingException("Could not determine presence of change log table", e);
        }
    }

    public List<Long> getAppliedChanges() {
        if (changelogTableExists()) {
            try {
                ResultSet rs = queryExecuter.executeQuery(
                        "SELECT change_number FROM " + changeLogTableName + "  ORDER BY change_number");

                List<Long> changeNumbers = new ArrayList<Long>();

                while (rs.next()) {
                    changeNumbers.add(rs.getLong(1));
                }

                rs.close();

                return changeNumbers;
            } catch (SQLException e) {
                throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
                        + e.getMessage(), e);
            }
        } else {
            return Collections.emptyList();
        }
	}

    public String getChangelogDeleteSql(ChangeScript script) {
		return String.format(
			"DELETE FROM " + changeLogTableName + " WHERE change_number = %d",
				script.getId());
	}

    public void recordScriptApplied(ChangeScript script) {
        try {
            queryExecuter.execute(
                    "INSERT INTO " + changeLogTableName + " (change_number, complete_dt, applied_by, description)" +
                            " VALUES (?, ?, ?, ?)",
                    script.getId(),
                    new Timestamp(timeProvider.now().getTime()),
                    queryExecuter.getDatabaseUsername(),
                    script.getDescription()
                    );
        } catch (SQLException e) {
            throw new SchemaVersionTrackingException("Could not update change log because: "
                    + e.getMessage(), e);
        }
    }

    public void setTimeProvider(CurrentTimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    public static class CurrentTimeProvider {

        public Date now() {
            return new Date();
        }
    }
}
