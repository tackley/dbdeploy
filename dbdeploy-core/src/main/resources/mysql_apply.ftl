[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="scripts" type="java.util.List<com.dbdeploy.scripts.ChangeScript>" --]
[#list scripts as script]

-- START CHANGE SCRIPT ${script}

START TRANSACTION;

${script.content}

INSERT INTO ${changeLogTableName} (change_number, complete_dt, applied_by, description)
 VALUES (${script.id}, CURRENT_TIMESTAMP, USER(), '${script.description}');

COMMIT;

-- END CHANGE SCRIPT ${script}

[/#list]