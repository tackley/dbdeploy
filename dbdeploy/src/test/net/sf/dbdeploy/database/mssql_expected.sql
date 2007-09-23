

--------------- Fragment begins: #1 ---------------
INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (1, 'null', getdate(), user_name(), '1: change1.sql')
GO
COMMIT
GO


-- Change script: #1
-- contents of change script 1

UPDATE changelog SET complete_dt = getdate() WHERE change_number = 1 AND delta_set = 'null'
GO
COMMIT
GO
--------------- Fragment ends: #1 ---------------


--------------- Fragment begins: #2 ---------------
INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (2, 'null', getdate(), user_name(), '2: change2.sql')
GO
COMMIT
GO


-- Change script: #2
-- contents of change script 2

UPDATE changelog SET complete_dt = getdate() WHERE change_number = 2 AND delta_set = 'null'
GO
COMMIT
GO
--------------- Fragment ends: #2 ---------------

