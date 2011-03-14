
-- Most databases don't apply DDL statements transactionally.
-- Therefore, to recover from failure more easily, only put a single DDL
-- statement in each change script.

CREATE TABLE Test (id INTEGER, data VARCHAR(100));

--//@UNDO

DROP TABLE Test;