
-- START CHANGE SCRIPT #1: 001_change.sql

-- contents of change script 1

INSERT INTO changelog (change_number, complete_dt, applied_by, description)
 VALUES (1, getdate(), user_name(), '001_change.sql')
GO

-- END CHANGE SCRIPT #1: 001_change.sql


-- START CHANGE SCRIPT #2: 002_change.sql

-- contents of change script 2

INSERT INTO changelog (change_number, complete_dt, applied_by, description)
 VALUES (2, getdate(), user_name(), '002_change.sql')
GO

-- END CHANGE SCRIPT #2: 002_change.sql

