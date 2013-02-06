[#ftl]
[#-- @ftlvariable name="changeLogTableName" type="java.lang.String" --]
[#-- @ftlvariable name="delimiter" type="java.lang.String" --]
[#-- @ftlvariable name="separator" type="java.lang.String" --]

-- Create the schema version table

CREATE TABLE ${changeLogTableName} (
  change_number NUMERIC(22,0) NOT NULL,
  complete_dt TIMESTAMP WITH TIME ZONE NOT NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
)${separator}${delimiter}

ALTER TABLE ${changeLogTableName} ADD CONSTRAINT Pk${changeLogTableName} PRIMARY KEY (change_number)${delimiter}${separator}
