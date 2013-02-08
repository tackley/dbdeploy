
-- Create the schema version table

CREATE TABLE changelog (
  change_number BIGINT NOT NULL,
  complete_dt TIMESTAMP WITH TIME ZONE NOT NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
);

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number);

-- START CHANGE SCRIPT #1: 001_change.sql

-- contents of change script 1

INSERT INTO changelog (change_number, complete_dt, applied_by, description)
 VALUES (1, current_timestamp, current_user, '001_change.sql');

COMMIT;

-- END CHANGE SCRIPT #1: 001_change.sql


-- START CHANGE SCRIPT #2: 002_change.sql

-- contents of change script 2

INSERT INTO changelog (change_number, complete_dt, applied_by, description)
 VALUES (2, current_timestamp, current_user, '002_change.sql');

COMMIT;

-- END CHANGE SCRIPT #2: 002_change.sql

