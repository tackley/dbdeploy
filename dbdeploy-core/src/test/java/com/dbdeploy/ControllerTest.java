package com.dbdeploy;

import com.dbdeploy.scripts.ChangeScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnit44Runner;

import java.util.Arrays;
import java.util.Collections;

import com.dbdeploy.AppliedChangesProvider;
import com.dbdeploy.AvailableChangeScriptsProvider;
import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.Controller;

@RunWith(MockitoJUnit44Runner.class)
public class ControllerTest {

	@Mock private AvailableChangeScriptsProvider availableChangeScriptsProvider;
	@Mock private AppliedChangesProvider appliedChangesProvider;
	@Mock private ChangeScriptApplier applier;
	@Mock private ChangeScriptApplier undoApplier;
	private Controller controller;
	private ChangeScript change1;
	private ChangeScript change2;
	private ChangeScript change3;

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
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(Integer.MAX_VALUE);

		InOrder o = inOrder(applier);
		o.verify(applier).apply(change1);
		o.verify(applier).apply(change2);
		o.verify(applier).apply(change3);
	}

	@Test
	public void shouldNotCrashWhenPassedANullUndoApplier() throws Exception {
		controller = new Controller(availableChangeScriptsProvider, appliedChangesProvider, applier, null);

		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(Integer.MAX_VALUE);
	}

	@Test
	public void shouldApplyUndoScriptsInReverseOrder() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(Integer.MAX_VALUE);

		InOrder o = inOrder(undoApplier);
		o.verify(undoApplier).apply(change3);
		o.verify(undoApplier).apply(change2);
		o.verify(undoApplier).apply(change1);
	}


	@Test
	public void shouldIgnoreChangesAlreadyAppliedToTheDatabase() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Arrays.asList(1));

		controller.processChangeScripts(Integer.MAX_VALUE);

		InOrder o = inOrder(applier);
		o.verify(applier, never()).apply(change1);
		o.verify(applier).apply(change2);
		o.verify(applier).apply(change3);
	}

	@Test
	public void shouldNotApplyChangesGreaterThanTheMaxChangeToApply() throws Exception {
		when(appliedChangesProvider.getAppliedChanges()).thenReturn(Collections.<Integer>emptyList());

		controller.processChangeScripts(2);

		InOrder o = inOrder(applier);
		o.verify(applier).apply(change1);
		o.verify(applier).apply(change2);
		o.verify(applier, never()).apply(change3);
	}

	@Test
	public void shouldCallBeginAndEndOnTheApplier() throws Exception {
		controller.processChangeScripts(Integer.MAX_VALUE);

		InOrder o = inOrder(applier);
		o.verify(applier).begin();
		o.verify(applier).apply(change1);
		o.verify(applier).apply(change2);
		o.verify(applier).apply(change3);
		o.verify(applier).end();
	}





}
