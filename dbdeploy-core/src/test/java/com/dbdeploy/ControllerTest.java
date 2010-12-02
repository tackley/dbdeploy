package com.dbdeploy;

import com.dbdeploy.appliers.ApplyMode;
import com.dbdeploy.appliers.ChangeScriptApplier;
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

    @Before
	public void setUp() {
		controller = new Controller(availableChangeScriptsProvider, appliedChangesProvider, applier);

		change1 = new ChangeScript(1);
		change2 = new ChangeScript(2);
		change3 = new ChangeScript(3);

        when(availableChangeScriptsProvider.getAvailableChangeScripts())
				.thenReturn(Arrays.asList(change1, change2, change3));
	}

	@Test
	public void shouldApplyChangeScriptsInOrder() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(Integer.MAX_VALUE);

        assertThat(applier.changeScripts.size(), is(3));
		assertThat(applier.changeScripts.get(0), is(change1));
		assertThat(applier.changeScripts.get(1), is(change2));
		assertThat(applier.changeScripts.get(2), is(change3));
	}

	@Test
	public void shouldNotCrashWhenPassedANullUndoApplier() throws Exception {
	    controller = new Controller(availableChangeScriptsProvider, appliedChangesProvider, applier);
	    when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(Integer.MAX_VALUE);
	}

	@Test
	public void shouldApplyUndoScriptsInReverseOrder() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1,2,3));

		controller.processChangeScripts(0);

        assertThat(applier.changeScripts.size(), is(3));
		assertThat(applier.changeScripts.get(0), is(change3));
		assertThat(applier.changeScripts.get(1), is(change2));
		assertThat(applier.changeScripts.get(2), is(change1));
	}
	
	@Test
    public void shouldApplyPartialUndoScriptsInReverseOrder() throws Exception {
        when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1,2,3));

        controller.processChangeScripts(1);

        assertThat(applier.changeScripts.size(), is(2));
        assertThat(applier.changeScripts.get(0), is(change3));
        assertThat(applier.changeScripts.get(1), is(change2));
    }
	
	@Test
    public void shouldApplyPartialUndoScriptsInReverseOrder2() throws Exception {
        when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1,3));

        controller.processChangeScripts(0);

        assertThat(applier.changeScripts.size(), is(2));
        assertThat(applier.changeScripts.get(0), is(change3));
        assertThat(applier.changeScripts.get(1), is(change1));
    }


	@Test
	public void shouldIgnoreChangesAlreadyAppliedToTheDatabase() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1));

		controller.processChangeScripts(Integer.MAX_VALUE);

        assertThat(applier.changeScripts.size(), is(2));
		assertThat(applier.changeScripts.get(0), is(change2));
		assertThat(applier.changeScripts.get(1), is(change3));
	}

	@Test
	public void shouldNotApplyChangesGreaterThanTheMaxChangeToApply() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(2);

        assertThat(applier.changeScripts.size(), is(2));
		assertThat(applier.changeScripts.get(0), is(change1));
		assertThat(applier.changeScripts.get(1), is(change2));
	}


	private class StubChangeScriptApplier implements ChangeScriptApplier {
        private List<ChangeScript> changeScripts;
        private ApplyMode applyMode;

        public void apply(List<ChangeScript> changeScripts, ApplyMode applyMode) {
            this.changeScripts = new ArrayList<ChangeScript>(changeScripts);
            this.applyMode = applyMode;
        }

    }
}
