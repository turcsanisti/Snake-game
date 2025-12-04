package nhf.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import nhf.core.Scores;

public class ScoresEntryTest {

    @Test
    public void testEntryDataRetrieval() {
        Scores.Entry entry = new Scores.Entry("Player1", 100, "01:00", "Win");
        assertAll(
            () -> assertEquals("Player1", entry.getName()),
            () -> assertEquals(100, entry.getScore()),
            () -> assertEquals("01:00", entry.getTime()),
            () -> assertEquals("Win", entry.getResult())
        );
    }

    @Test
    public void testCompareToHigherScore() {
        Scores.Entry high = new Scores.Entry("A", 200, "01:00", "Win");
        Scores.Entry low = new Scores.Entry("B", 100, "01:00", "Win");
        assertTrue(high.compareTo(low) < 0);
    }

    @Test
    public void testCompareToEqualScore() {
        Scores.Entry e1 = new Scores.Entry("A", 100, "01:00", "Win");
        Scores.Entry e2 = new Scores.Entry("B", 100, "02:00", "Lose");
        assertEquals(0, e1.compareTo(e2));
    }
}