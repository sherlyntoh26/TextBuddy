import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TextBuddyTest {

	@Before
	// Initialize data
	public void initialize() {
		testCommand("Test Clear", "all content deleted from default.txt", "clear");
		testCommand("Test Add Text", "added to default.txt: \"simple test\"", "add simple test");
		testCommand("Test Add Text", "added to default.txt: \"Yesterday was\"", "add Yesterday was");
		testCommand("Test Add Text", "added to default.txt: \"\"", "add ");
	}

	@Test
	// Testing: Add & Display
	public void testAddDisplay() {
		testCommand("Test Display", "1. simple test\n2. Yesterday was\n3. ", "display");
		testCommand("Test Add Text", "added to default.txt: \"Fine Day\"", "add Fine Day");
		testCommand("Test Display", "1. simple test\n2. Yesterday was\n3. \n4. Fine Day", "display");
	}

	@Test
	// Testing: Delete & Display
	public void testDeleteDisplay() {
		testCommand("Test Display", "1. simple test\n2. Yesterday was\n3. ", "display");
		testCommand("Test Delete", "deleted from default.txt: \"Yesterday was\"", "delete 2");
		testCommand("Test Display", "1. simple test\n2. ", "display");
		testCommand("Test Invalid Delete", "Line delete 9 does not exist in default.txt", "delete 9");
		testCommand("Test Delete", "deleted from default.txt: \"simple test\"", "delete 1");
		testCommand("Test Display", "1. ", "display");
	}

	@Test
	// Testing: Invalid Command
	public void testInvalidCommand() {
		testCommand("Test invalid command", "invalid command format: delete a", "delete a");
		testCommand("Test invalid command", "invalid command format: delete really", "delete really");
		testCommand("Test invalid command", "invalid command format: haha", "haha");
	}

	@Test
	// Testing: Sort & Display
	public void testSortDisplay() {
		testCommand("Test Sort", "Contents in default.txt have been sorted", "sort");
		testCommand("Test Display", "1. \n2. simple test\n3. Yesterday was", "display");
		testCommand("Test Add Text", "added to default.txt: \"hello\"", "add hello");
		testCommand("Test Display", "1. \n2. simple test\n3. Yesterday was\n4. hello", "display");
		testCommand("Test Sort", "Contents in default.txt have been sorted", "sort");
		testCommand("Test Display", "1. \n2. hello\n3. simple test\n4. Yesterday was", "display");
		testCommand("Test Clear", "all content deleted from default.txt", "clear");
		testCommand("Test Sort Empty File", "default.txt has nothing to sort", "sort");
	}

	@Test
	// Testing: Search
	public void testSearch() {
		testCommand("Test Search", "1. Yesterday was", "search was");
		testCommand("Test Add Text", "added to default.txt: \"yesterday was sunny\"", "add yesterday was sunny");
		testCommand("Test Search", "1. Yesterday was\n2. yesterday was sunny", "search was");
		testCommand("Test Search", "1. Yesterday was\n2. yesterday was sunny", "search yesterday");
		testCommand("Test Invalid Search", "\"hello\" cannot be found in default.txt", "search hello");
		testCommand("Test Clear", "all content deleted from default.txt", "clear");
		testCommand("Test Invalid Search", "\"yesterday\" cannot be found in default.txt", "search yesterday");
	}

	@Test
	// Testing: Clear & Display
	public void testClearDisplay() {
		testCommand("Test Clear", "all content deleted from default.txt", "clear");
		testCommand("Test Empty Display", "default.txt is empty", "display");
	}

	public void testCommand(String description, String expected, String command) {
		assertEquals(description, expected, TextBuddy.executeCommand(command));
	}
}
