package net.intelie.challenges;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ EventQueryTest.class, EventStorageTest.class, EventTest.class })
public class AllTests {

}
