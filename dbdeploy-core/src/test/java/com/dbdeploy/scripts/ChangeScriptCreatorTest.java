package com.dbdeploy.scripts;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author jbogan
 */
public class ChangeScriptCreatorTest {
    private ChangeScriptCreator changeScriptCreator;
    private File scriptDirectory;

    @Before
    public void setUp() {
        scriptDirectory = new File("target");
        changeScriptCreator = new ChangeScriptCreator();
        changeScriptCreator.setScriptDirectory(scriptDirectory);
    }

    @Test
    public void defaultChangeScriptCreatorCreatesScriptWithTimestamp() throws Exception {
        final File newChangeScript = changeScriptCreator.createScript();
        assertTrue(newChangeScript.exists());

        final String newChangeScriptFileName = newChangeScript.getName();
        assertTrue("file name doesnt have sql suffix", newChangeScriptFileName.endsWith(".sql"));
        assertTrue("file name not timestamped correctly", newChangeScriptFileName.matches("[0-9]{12}\\.sql"));
    }

    @Test
    public void createsScriptWithTimestampAndDescription() throws Exception {
        final String scriptDescription = "test_1234";
        changeScriptCreator.setScriptDescription(scriptDescription);
        final File newChangeScript = changeScriptCreator.createScript();

        final String newChangeScriptFileName = newChangeScript.getName();
        assertTrue("file name not timestamped and named correctly", newChangeScriptFileName.matches("[0-9]{12}_" + scriptDescription + "\\.sql"));
    }
}