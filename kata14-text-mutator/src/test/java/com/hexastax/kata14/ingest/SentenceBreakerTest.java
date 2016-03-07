package com.hexastax.kata14.ingest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.hexastax.kata14.ingest.SentenceBreaker;

@RunWith(value = Parameterized.class)
public class SentenceBreakerTest {

  private String input;
  private List<String> expectedSentences;

  @Parameters
  public static Collection<Object[]> data() {
    Object[][] data = new Object[][] {

        {
            "First sentence.",
            Arrays.asList(new String[] { "First sentence." })
        },
        {
            "  First sentence. Second sentence. ",
            Arrays.asList(new String[] { "First sentence.", "Second sentence." })
        },
        {
            "First sentence! Second sentence?",
            Arrays.asList(new String[] { "First sentence!", "Second sentence?" })
        },
        {
            "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.",
            Arrays.asList(new String[] {
                "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29.",
                "Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group.",
                "Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate."
            })
        },
        {
            "the quick brown fox jumps over the lazy dog",
            Arrays.asList(new String[] { "the quick brown fox jumps over the lazy dog" })
        }

    };
    return Arrays.asList(data);
  }

  public SentenceBreakerTest(String input, List<String> expectedSentences) {
    this.input = input;
    this.expectedSentences = expectedSentences;
  }

  @Test
  public void testSentenceBreaking() throws IOException {
    SentenceBreaker sb = new SentenceBreaker();
    List<String> actualSentences = new ArrayList<String>();
    for (Iterator<String> senIter = sb.iterator(input); senIter.hasNext();) {
      actualSentences.add(senIter.next());
    }
    Assert.assertEquals(expectedSentences, actualSentences);
  }

}
