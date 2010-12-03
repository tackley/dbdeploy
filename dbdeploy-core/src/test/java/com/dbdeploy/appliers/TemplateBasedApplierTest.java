package com.dbdeploy.appliers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.dbdeploy.exceptions.UsageException;

public class TemplateBasedApplierTest {

	@Test
	public void shouldThrowUsageExceptionWhenTemplateNotFound() throws Exception {
		File outfile = new File("outfile");
		outfile.deleteOnExit();
		
        TemplateBasedApplier applier = new TemplateBasedApplier(outfile , "some_complete_rubbish", null, null);
		try {
			applier.apply(null, null);
			Assert.fail("expected exception");
		} catch (UsageException e) {
			assertThat(e.getMessage(), is("Could not find template named some_complete_rubbish_apply.ftl\n" +
					"Check that you have got the name of the database syntax correct."));
		}
	}
}
