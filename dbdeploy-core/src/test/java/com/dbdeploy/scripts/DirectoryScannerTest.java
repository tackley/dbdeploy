package com.dbdeploy.scripts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class DirectoryScannerTest {

	@Test
	public void shouldIgnoreChangeScriptsThatAreInTheIgnoreList() throws Exception {
		DirectoryScanner scanner = new DirectoryScanner("UTF-8");
		List<Long> changeScriptIdsToIgnore = Arrays.asList(new Long[] { 2l });
		
		File file1 = mockFile("1_file1");
		File file2 = mockFile("2_file2");
		File file3 = mockFile("3_file3");
		File[] files = new File[] { file1, file2, file3 };
		
		File directory =  Mockito.mock(File.class);
		when(directory.listFiles()).thenReturn(files);
		
		List<ChangeScript> changeScripts = scanner.getChangeScriptsForDirectory(directory, changeScriptIdsToIgnore);
		
		for (ChangeScript changeScript : changeScripts) {
			assertThat(changeScript.getFile(), not(equalTo(file2)));
		}
	}

	private File mockFile(String fileName) {
		File file = Mockito.mock(File.class);
		when(file.isFile()).thenReturn(true);
		when(file.getName()).thenReturn(fileName);
		return file;
	}
	
}
