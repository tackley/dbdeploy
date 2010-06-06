package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.UsageException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Responsible for creating a new change script file
 * to be used by dbdeploy.  This class will generate
 * a new change script using a timestamp as the change
 * script number and any supplied text as the rest
 * of the filename.
 *
 * @author jbogan
 */
public class ChangeScriptCreator {
    private String changeScriptSuffix = ".sql";
    private String changeScriptTimestampFormat = "yyyyMMddHHmm";
    private String scriptDescription;
    private File scriptDirectory;
    private DateFormat dateFormatter;

    public ChangeScriptCreator() {
        dateFormatter = new SimpleDateFormat(changeScriptTimestampFormat);
    }

    public File go() throws IOException {
        validate();

        return createScript();
    }

    private void validate() {
        if (scriptDirectory == null || !scriptDirectory.isDirectory()) {
            throw new UsageException("Script directory must point to a valid directory");
        }
    }

    public File createScript() throws IOException {
        final String newScriptFileName = getChangeScriptFileName();
        final String fullScriptPath = scriptDirectory + File.separator + newScriptFileName;

        final File newChangeScriptFile = new File(fullScriptPath);
        if (newChangeScriptFile.createNewFile()) {
            return newChangeScriptFile;
        } else {
            throw new IOException("Unable to create new change script " + fullScriptPath);
        }
    }

    private String getChangeScriptFileName() {
        final StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(getFileTimestamp());
        if (scriptDescription != null && !scriptDescription.equals("")) {
            fileNameBuilder.append("_");
            fileNameBuilder.append(scriptDescription);
        }
        fileNameBuilder.append(changeScriptSuffix);
        
        return fileNameBuilder.toString();
    }

    private String getFileTimestamp() {
        return dateFormatter.format(new Date());
    }
    
    public void setScriptDescription(final String scriptDescription) {
        this.scriptDescription = scriptDescription;
    }

    public void setScriptDirectory(final File scriptDirectory) {
        this.scriptDirectory = scriptDirectory;
    }

    public static void main(String[] args) {
        ChangeScriptCreator creator = new ChangeScriptCreator();
        
        try {
            parseArguments(args, creator);
            creator.go();
        } catch (UsageException ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.err.println("Usage: java " + creator.getClass().getName() + " scriptDirectory [scriptName]");
        } catch (Exception ex) {
            System.err.println("Failed to create script: " + ex);
            ex.printStackTrace();
            System.exit(2);
        }

        System.exit(0);
    }

    private static void parseArguments(String[] args, ChangeScriptCreator creator) {
        if (args.length >= 1) {
            final String scriptDirectoryPath = args[0];
            creator.setScriptDirectory(new File(scriptDirectoryPath));
        }

        if (args.length >= 2) {
            final String scriptDescription = args[1];
            creator.setScriptDescription(scriptDescription);
        }
    }
}
