package com.dbdeploy;

import com.dbdeploy.exceptions.UsageException;
import org.apache.commons.cli.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;

import com.dbdeploy.DbDeploy;

public class DbDeployCommandLineParser {
	public void parse(String[] args, DbDeploy dbDeploy) throws UsageException {
		try {
			dbDeploy.setScriptdirectory(new File("."));
			final CommandLine commandLine = getParser().parse(getOptions(), args);
			copyValuesFromCommandLineToDbDeployBean(dbDeploy, commandLine);
		} catch (ParseException e) {
			throw new UsageException(e.getMessage(), e);
		}
	}

	private void copyValuesFromCommandLineToDbDeployBean(DbDeploy dbDeploy, CommandLine commandLine) {

		try {
			final BeanInfo info = Introspector.getBeanInfo(dbDeploy.getClass());

			for (PropertyDescriptor p : info.getPropertyDescriptors()) {
				final String propertyName = p.getDisplayName();
//				System.out.println("Property: " + propertyName);
				if (commandLine.hasOption(propertyName)) {
//					System.out.println(" ^ assigning!");
					Object value = commandLine.getOptionValue(propertyName);
					if (p.getPropertyType().isAssignableFrom(File.class)) {
						value = new File((String) value);
					}

//					System.out.println(" ^^ value is " + value + " (" + value.getClass() + ")");
					p.getWriteMethod().invoke(dbDeploy, value);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("dbdeploy", getOptions());

	}

	private CommandLineParser getParser() {
		return new GnuParser();
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
				.hasArg()
				.withDescription("database password")
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
				.withDescription("delta set name (default: Main)")
				.withLongOpt("deltaset")
				.create());

		return options;
	}
}
