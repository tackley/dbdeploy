

--------------- Fragment begins: #1 ---------------

-- Change script: #1
-- contents of change script 1

INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) VALUES (1, 'null', CURRENT_TIMESTAMP, USER(), '1: change1.sql');
COMMIT;
--------------- Fragment ends: #1 ---------------


--------------- Fragment begins: #2 ---------------

-- Change script: #2
-- contents of change script 2

INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) VALUES (2, 'null', CURRENT_TIMESTAMP, USER(), '2: change2.sql');
COMMIT;
--------------- Fragment ends: #2 ---------------

