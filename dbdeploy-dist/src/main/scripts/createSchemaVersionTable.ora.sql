CREATE TABLE changelog (
  change_number NUMBER(22,0) NOT NULL,
  complete_dt TIMESTAMP NOT NULL,
  applied_by VARCHAR2(100) NOT NULL,
  description VARCHAR2(500) NOT NULL
);

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
;