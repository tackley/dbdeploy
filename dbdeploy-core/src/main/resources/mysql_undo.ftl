[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="scripts" type="java.util.List<com.dbdeploy.scripts.ChangeScript>" --]
[#list scripts as script]

-- START UNDO OF CHANGE SCRIPT ${script}

START TRANSACTION;

${script.undoContent}

DELETE FROM ${changeLogTableName} WHERE change_number = ${script.id?c};

COMMIT;

-- END UNDO OF CHANGE SCRIPT ${script}

[/#list]