[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="scripts" type="java.util.List<com.dbdeploy.scripts.ChangeScript>" --]
[#list scripts as script]

-- START UNDO OF CHANGE SCRIPT ${script}

BEGIN TRANSACTION
GO

${script.undoContent}

DELETE FROM ${changeLogTableName} WHERE change_number = ${script.id}
GO

COMMIT
GO

-- END UNDO OF CHANGE SCRIPT ${script}

[/#list]