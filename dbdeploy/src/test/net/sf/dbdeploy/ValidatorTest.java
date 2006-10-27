package net.sf.dbdeploy;

import org.apache.tools.ant.BuildException;

import junit.framework.TestCase;


public class ValidatorTest extends TestCase {
	private static final String USER_ID = "userId";
	private static final String DRIVER = "driver";
	private static final String URL = "url";
	private static final String DBMS = "ora";
	private static final String DIR = "dir";
	private static final String OUTPUT_FILE = "output file";

	public void testShouldFailWhenPassedNullUserid() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(null, DRIVER, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Userid expected", e.getMessage().substring(37,52));
		}
		
	}
	
	public void testShouldFailWhenPassedEmptyUserid() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate("", DRIVER, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Userid expected", e.getMessage().substring(37,52));
		}
		
	}
	
	public void testShouldFailWhenPassedNullDriver() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, null, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Driver expected", e.getMessage().substring(37,52));
		}
		
	}
	
	public void testShouldFailWhenPassedEmptyDriver() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, "", URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Driver expected", e.getMessage().substring(37,52));
		}
		
	}
	
	public void testShouldFailWhenPassedNullUrl() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, null, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Url expected", e.getMessage().substring(37,49));
		}
		
	}
	
	public void testShouldFailWhenPassedEmptyUrl() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, "", DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Url expected", e.getMessage().substring(37,49));
		}
		
	}
	
	public void testShouldFailWhenPassedNonOraDbms() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, "nothing", DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Unknown DBMS", e.getMessage().substring(37,49));
		}
		
	}
	
	public void testShouldFailWhenPassedNullDir() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, null, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Dir expected", e.getMessage().substring(37,49));
		}
		
	}
	
	public void testShouldFailWhenPassedEmptyDir() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, "", OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Dir expected", e.getMessage().substring(37,49));
		}
		
	}
	
	public void testShouldFailWhenPassedNullOutputFile() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, DIR, null);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Output file expected", e.getMessage().substring(37,57));
		}
		
	}
	
	public void testShouldFailWhenPassedEmptyOutputFile() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, DIR, "");
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Output file expected", e.getMessage().substring(37,57));
		}
		
	}
}
