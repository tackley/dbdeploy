package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnit44Runner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnit44Runner.class)
public class ControllerTest {

	@Mock private AvailableChangeScriptsProvider availableChangeScriptsProvider;
	@Mock private AppliedChangesProvider appliedChangesProvider;
	private Controller controller;
	private ChangeScript change1;
	private ChangeScript change2;
	private ChangeScript change3;

    private StubChangeScriptApplier applier = new StubChangeScriptApplier();
    private StubChangeScriptApplier undoApplier = new StubChangeScriptApplier();

    @Before
	public void setUp() {
		controller = new Controller(availableChangeScriptsProvider, appliedChangesProvider, applier, undoApplier);

		change1 = new ChangeScript(1);
		change2 = new ChangeScript(2);
		change3 = new ChangeScript(3);

        when(availableChangeScriptsProvider.getAvailableChangeScripts())
				.thenReturn(Arrays.asList(change1, change2, change3));
	}

	@Test
	public void shouldApplyChangeScriptsInOrder() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Long>emptyList());

		controller.processChangeScripts(Long.MAX_VALUE);

        assertThat(applier.changeScripts.size(), is(3));
		assertThat(applier.changeScripts.get(0), is(change1));
		assertThat(applier.changeScripts.get(1), is(change2));
		assertThat(applier.changeScripts.get(2), is(change3));
	}

	@Test
	public void shouldNotCrashWhenPassedANullUndoApplier() throws Exception {
		controller = new Controller(availableChangeScriptsProvider, appliedChangesProvider, applier, null);

		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Long>emptyList());

		controller.processChangeScripts(Long.MAX_VALUE);
	}

	@Test
	public void shouldApplyUndoScriptsInReverseOrder() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Long>emptyList());

		controller.processChangeScripts(Long.MAX_VALUE);

        assertThat(undoApplier.changeScripts.size(), is(3));
		assertThat(undoApplier.changeScripts.get(0), is(change3));
		assertThat(undoApplier.changeScripts.get(1), is(change2));
		assertThat(undoApplier.changeScripts.get(2), is(change1));
	}


	@Test
	public void shouldIgnoreChangesAlreadyAppliedToTheDatabase() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1L));

		controller.processChangeScripts(Long.MAX_VALUE);

        assertThat(applier.changeScripts.size(), is(2));
		assertThat(applier.changeScripts.get(0), is(change2));
		assertThat(applier.changeScripts.get(1), is(change3));
	}

	@Test
	public void shouldNotApplyChangesGreaterThanTheMaxChangeToApply() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Long>emptyList());

		controller.processChangeScripts(2L);

        assertThat(applier.changeScripts.size(), is(2));
		assertThat(applier.changeScripts.get(0), is(change1));
		assertThat(applier.changeScripts.get(1), is(change2));
	}



    private class StubChangeScriptApplier implements ChangeScriptApplier {
        private List<ChangeScript> changeScripts;

        public void apply(List<ChangeScript> changeScripts) {
            this.changeScripts = new ArrayList<ChangeScript>(changeScripts);
        }

    }
}
