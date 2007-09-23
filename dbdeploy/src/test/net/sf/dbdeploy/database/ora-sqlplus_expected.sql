WHENEVER SQLERROR EXIT sql.sqlcode ROLLBACK
SET DEFINE OFF

--------------- Fragment begins: #1 ---------------
INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (1, 'null', CURRENT_TIMESTAMP, USER, '1: change1.sql');
COMMIT;


-- Change script: #1
-- contents of change script 1

UPDATE changelog SET complete_dt = CURRENT_TIMESTAMP WHERE change_number = 1 AND delta_set = 'null';
COMMIT;
--------------- Fragment ends: #1 ---------------


--------------- Fragment begins: #2 ---------------
INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) VALUES (2, 'null', CURRENT_TIMESTAMP, USER, '2: change2.sql');
COMMIT;


-- Change script: #2
-- contents of change script 2

UPDATE changelog SET complete_dt = CURRENT_TIMESTAMP WHERE change_number = 2 AND delta_set = 'null';
COMMIT;
--------------- Fragment ends: #2 ---------------

