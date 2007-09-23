package net.sf.dbdeploy;

import org.apache.tools.ant.BuildException;
import static org.junit.Assert.*;
import org.junit.Test;

public class ValidatorTest {
	private static final String USER_ID = "userId";
	private static final String DRIVER = "driver";
	private static final String URL = "url";
	private static final String DBMS = "ora";
	private static final String DIR = "dir";
	private static final String OUTPUT_FILE = "output file";
	private final Validator validator;

	public ValidatorTest() {
		validator = new Validator();
		validator.setUsage("ant");
	}

	@Test
	public void shouldFailWhenPassedNullUserid() {
		try {
			validator.validate(null, DRIVER, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Userid expected", e.getMessage().substring(37,52));
		}
		
	}

	@Test
	public void shouldFailWhenPassedEmptyUserid() {
		try {
			validator.validate("", DRIVER, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Userid expected", e.getMessage().substring(37,52));
		}
		
	}

	@Test
	public void shouldFailWhenPassedNullDriver() {
		try {
			validator.validate(USER_ID, null, URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Driver expected", e.getMessage().substring(37,52));
		}
		
	}

	@Test
	public void shouldFailWhenPassedEmptyDriver() {
		try {
			validator.validate(USER_ID, "", URL, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Driver expected", e.getMessage().substring(37,52));
		}
		
	}

	@Test
	public void shouldFailWhenPassedNullUrl() {
		try {
			validator.validate(USER_ID, DRIVER, null, DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Url expected", e.getMessage().substring(37,49));
		}
		
	}

	@Test
	public void shouldFailWhenPassedEmptyUrl() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, "", DBMS, DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Url expected", e.getMessage().substring(37,49));
		}
		
	}

	@Test
	public void shouldFailWhenPassedNonOraDbms() {
		Validator validator = new Validator();
		validator.setUsage("ant");
		try {
			validator.validate(USER_ID, DRIVER, URL, "nothing", DIR, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Unknown DBMS", e.getMessage().substring(37,49));
		}
		
	}

	@Test
	public void shouldFailWhenPassedNullDir() {
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, null, OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Dir expected", e.getMessage().substring(37,49));
		}
		
	}

	@Test
	public void shouldFailWhenPassedEmptyDir() {
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, "", OUTPUT_FILE);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Dir expected", e.getMessage().substring(37,49));
		}
		
	}

	@Test
	public void shouldFailWhenPassedNullOutputFile() {
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, DIR, null);
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Output file expected", e.getMessage().substring(37,57));
		}
		
	}
	
	@Test
	public void shouldFailWhenPassedEmptyOutputFile() {
		try {
			validator.validate(USER_ID, DRIVER, URL, DBMS, DIR, "");
			fail("BuildException expected");
		} catch (BuildException e) {
			assertEquals("Output file expected", e.getMessage().substring(37,57));
		}
		
	}
}
