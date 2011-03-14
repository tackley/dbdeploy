package com.dbdeploy;

import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.database.DelimiterType;
import org.apache.commons.cli.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;

public class DbDeployCommandLineParser {
    private final UserInputReader userInputReader;

    public DbDeployCommandLineParser() {
        this(new UserInputReader());
    }

    public DbDeployCommandLineParser(UserInputReader userInputReader) {
        this.userInputReader = userInputReader;
    }

    public void parse(String[] args, DbDeploy dbDeploy) throws UsageException {
		try {
			dbDeploy.setScriptdirectory(new File("."));
            final CommandLine commandLine = new GnuParser().parse(getOptions(), args);
			copyValuesFromCommandLineToDbDeployBean(dbDeploy, commandLine);

            if (commandLine.hasOption("password") && commandLine.getOptionValue("password") == null) {
                dbDeploy.setPassword(userInputReader.read("Password"));
            }
		} catch (ParseException e) {
			throw new UsageException(e.getMessage(), e);
		}
	}

	private void copyValuesFromCommandLineToDbDeployBean(DbDeploy dbDeploy, CommandLine commandLine) {

		try {
			final BeanInfo info = Introspector.getBeanInfo(dbDeploy.getClass());

			for (PropertyDescriptor p : info.getPropertyDescriptors()) {
				final String propertyName = p.getDisplayName();
				if (commandLine.hasOption(propertyName)) {
					Object value = commandLine.getOptionValue(propertyName);
					if (p.getPropertyType().isAssignableFrom(File.class)) {
						value = new File((String) value);
					}

					p.getWriteMethod().invoke(dbDeploy, value);
				}
			}

			if (commandLine.hasOption("delimitertype")) {
				dbDeploy.setDelimiterType(DelimiterType.valueOf(commandLine.getOptionValue("delimitertype")));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("dbdeploy", getOptions());

	}

    @SuppressWarnings({"AccessStaticViaInstance"})
	private Options getOptions() {
		final Options options = new Options();

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("database user id")
				.withLongOpt("userid")
				.create("U"));

		options.addOption(OptionBuilder
				.hasOptionalArg()
				.withDescription("database password (use -P without a argument value to be prompted)")
				.withLongOpt("password")
				.create("P"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("database driver class")
				.withLongOpt("driver")
				.create("D"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("database url")
				.withLongOpt("url")
				.create("u"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("directory containing change scripts (default: .)")
				.withLongOpt("scriptdirectory")
				.create("s"));

	    options.addOption(OptionBuilder
			    .hasArg()
			    .withDescription("encoding for input and output files (default: UTF-8)")
			    .withLongOpt("encoding")
			    .create("e"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("output file")
				.withLongOpt("outputfile")
				.create("o"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("dbms type")
				.withLongOpt("dbms")
				.create("d"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("template directory")
				.withLongOpt("templatedir")
				.create());

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("name of change log table to use (default: changelog)")
				.withLongOpt("changeLogTableName")
				.create("t"));

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("delimiter to separate sql statements")
				.withLongOpt("delimiter")
				.create());

		options.addOption(OptionBuilder
				.hasArg()
				.withDescription("delimiter type to separate sql statements (row or normal)")
				.withLongOpt("delimitertype")
				.create());


		return options;
	}
}
