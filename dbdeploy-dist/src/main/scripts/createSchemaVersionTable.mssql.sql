CREATE TABLE changelog (
  change_number BIGINT NOT NULL,
  complete_dt DATETIME NOT NULL,
  applied_by VARCHAR(100) NOT NULL,
  description VARCHAR(500) NOT NULL
)
GO

ALTER TABLE changelog ADD CONSTRAINT Pkchangelog PRIMARY KEY (change_number)
GO