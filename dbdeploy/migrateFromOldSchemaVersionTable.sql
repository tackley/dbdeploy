DECLARE 
	highestVersion NUMBER;
BEGIN
	SELECT MAX(SchemaVersion) INTO highestVersion FROM DatabaseConfiguration;

    FOR i IN 1..highestVersion LOOP
        INSERT INTO CHANGELOG (change_number, description)
            VALUES (i, '(placeholder entry for script applied using old version of dbdeploy)');
    END LOOP

    COMMIT;
END;
/

