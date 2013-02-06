package com.dbdeploy.appliers;

	import com.dbdeploy.database.DelimiterType;
    import com.dbdeploy.database.QueryStatementSplitter;
    import com.dbdeploy.exceptions.UsageException;
import org.apache.commons.io.output.NullWriter;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TemplateBasedApplierTest {

	@Test
	public void shouldThrowUsageExceptionWhenTemplateNotFound() throws Exception {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.normal);
		TemplateBasedApplier applier = new TemplateBasedApplier(new NullWriter(), "some_complete_rubbish", null, null, splitter, null);
		try {
			applier.apply(null);
			Assert.fail("expected exception");
		} catch (UsageException e) {
			assertThat(e.getMessage(), is("Could not find template named some_complete_rubbish_apply.ftl\n" +
					"Check that you have got the name of the database syntax correct."));
		}
	}
}
