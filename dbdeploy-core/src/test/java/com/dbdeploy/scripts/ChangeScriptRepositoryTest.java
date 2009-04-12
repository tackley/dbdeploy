package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.DuplicateChangeScriptException;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.scripts.ChangeScriptRepository;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ChangeScriptRepositoryTest {

	@Test
	public void shouldReturnAnOrderedListOfChangeScripts() throws Exception {
		ChangeScript one = new ChangeScript(1);
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript four = new ChangeScript(4);
		
		ChangeScriptRepository repository = new ChangeScriptRepository(Arrays.asList( three, two, four, one ));
		
		List<ChangeScript> list = repository.getOrderedListOfDoChangeScripts();
		assertThat(4, equalTo(list.size()));
		assertSame(one, list.get(0));
		assertSame(two, list.get(1));
		assertSame(three, list.get(2));
		assertSame(four, list.get(3));
	}
	
	@Test
	public void shouldThrowWhenChangeScriptListContainsDuplicates() throws Exception {
		ChangeScript two = new ChangeScript(2);
		ChangeScript three = new ChangeScript(3);
		ChangeScript anotherTwo = new ChangeScript(2);
		
		try {
			new ChangeScriptRepository(Arrays.asList(three, two, anotherTwo));
			fail("expected exception");
		} catch (DuplicateChangeScriptException ex) {
			assertEquals("There is more than one change script with number 2", ex.getMessage());
		}
	}

	@Test
    public void shouldAllowChangeScriptsThatStartFromZero() throws Exception {
        ChangeScript zero = new ChangeScript(0);
        ChangeScript four = new ChangeScript(4);

        ChangeScriptRepository repository = new ChangeScriptRepository(Arrays.asList( zero, four ));

        List<ChangeScript> list = repository.getOrderedListOfDoChangeScripts();
        assertThat(2, equalTo(list.size()));
        assertSame(zero, list.get(0));
        assertSame(four, list.get(1));
    }
}
