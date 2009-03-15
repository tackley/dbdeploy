CREATE TABLE changelog (
  change_number INTEGER NOT NULL,
  delta_set VARCHAR2(10) NOT NULL,
  complete_dt TIMESTAMP NOT NULL,
  applied_by VARCHAR2(100) NOT NULL,
  description VARCHAR2(500) NOT NULL
);

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number, delta_set)
;