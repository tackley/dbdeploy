

--------------- Fragment begins: #1 ---------------

-- Change script: #1
-- contents of change script 1

INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) VALUES (1, 'null', getdate(), user_name(), '1: change1.sql')
GO
COMMIT
GO
--------------- Fragment ends: #1 ---------------


--------------- Fragment begins: #2 ---------------

-- Change script: #2
-- contents of change script 2

INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) VALUES (2, 'null', getdate(), user_name(), '2: change2.sql')
GO
COMMIT
GO
--------------- Fragment ends: #2 ---------------

