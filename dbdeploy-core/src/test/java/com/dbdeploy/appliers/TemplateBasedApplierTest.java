package com.dbdeploy.appliers;

import com.dbdeploy.exceptions.UsageException;
import org.apache.commons.io.output.NullOutputStream;
import static org.hamcrest.Matchers.is;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class TemplateBasedApplierTest {

	@Test
	public void shouldThrowUsageExceptionWhenTemplateNotFound() throws Exception {
		TemplateBasedApplier applier = new TemplateBasedApplier(new NullOutputStream(), "some_complete_rubbish", null, null);
		try {
			applier.apply(null);
			Assert.fail("expected exception");
		} catch (UsageException e) {
			assertThat(e.getMessage(), is("Could not find template named some_complete_rubbish_apply.ftl\n" +
					"Check that you have got the name of the database syntax correct."));
		}
	}
}
