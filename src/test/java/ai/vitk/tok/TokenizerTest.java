package ai.vitk.tok;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ai.vitk.type.Token;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class TokenizerTest {

    private Tokenizer tokenizer;

    @Before
    public void setup() {
        tokenizer = new Tokenizer();
    }

    @Test
    public void givenVietnamese_whenTokenizing_thenTokensReturned() {
        checkTokenization(
            "Hà Nội mùa này vắng những cơn mưa",
            "Hà Nội", "mùa", "này", "vắng", "những", "cơn", "mưa"
        );
        checkTokenization(
            "Việt Nam là quốc gia nằm ở phía Đông bán đảo Đông Dương thuộc khu vực Đông Nam Á",
            "Việt Nam", "là", "quốc gia", "nằm", "ở", "phía", "Đông", "bán đảo", "Đông Dương", "thuộc", "khu vực", "Đông Nam", "Á"
        );
    }
    
    @Test
    public void givenVietnamese_whenTokenizing_thenJoinedMatches() {
        checkJoinedMatches(
            "\ntest\n\n\ntest\n\n",
            "test test"
        );
        checkJoinedMatches(
            "phó tổng  QUERYTARGET evn QUERYTARGET  khẳng định: ‘giá điện sẽ tiếp tục tăng gấp đôi” - hà nội news vi.nnews.space/pho-tong- QUERYTARGET evn QUERYTARGET -k…",
            "phó_tổng QUERYTARGET evn QUERYTARGET khẳng_định : ‘ giá điện sẽ tiếp_tục tăng gấp đôi ” - hà_nội news vi.nnews.space / pho-tong - QUERYTARGET evn QUERYTARGET - k …"
        );
        checkJoinedMatches(
            "mebb:  QUERYTARGET ford QUERYTARGET  everest mới đến malaysia, giá bán cao hơn tại việt nam mebb.cf/mebb- QUERYTARGET ford QUERYTARGET -ever… pic.twitter.com/psb4cldnsi",
            "mebb : QUERYTARGET ford QUERYTARGET everest mới đến malaysia , giá bán cao hơn tại việt_nam mebb.cf / mebb - QUERYTARGET ford QUERYTARGET - ever … pic.twitter.com / psb4cldnsi"
        );
    }

    @Test
    public void givenVietnameseThatWouldBeNormalised_whenTokenizing_thenOriginalTokensReturned() {
        // kỹ would be normalised to kĩ internally
        checkTokenization(
            "Direct message để được chúng mình tư vấn kỹ hơn nhé",
            "Direct","message", "để", "được", "chúng mình", "tư vấn", "kỹ", "hơn", "nhé"
        );
    }

    private void checkTokenization(String text, String... expectedTokens) {
        assertTrue(checkTokenizationMatches(text, expectedTokens));
    }

    private void checkJoinedMatches(String text, String resulting) {
        final String actual = tokenize(text);
        Assert.assertEquals(resulting,  actual);
    }

    private String tokenize(String text) {
        final String actual = tokenizer.tokenize(text).stream()
            .map(Token::getWord)
            .map(token -> token.replaceAll("\\s+", "_"))
            .collect(Collectors.joining(" "));
        return actual;
    }
    
    private boolean checkTokenizationMatches(String text, String... expectedTokens) {
        List<Token> tokens = tokenizer.tokenize(text);
        List<String> actual = tokens.stream()
                .map(Token::getWord)
                .map(String::trim)
                .collect(Collectors.toList());
        return Arrays.asList(expectedTokens).equals(actual);
    }

    private Callable<String[]> checkLineTokenizationMatchesPromise(final String line) {
        return () -> {
            final String[] parts = line.split("\\t");
            return new String[] { parts[1], this.tokenize(parts[0])};
        };
    }
    
    private static final String FILE = "tokenized.tsv.gz";
    
    @Test
    public void testMultiThreaded() throws InterruptedException, ExecutionException, IOException {
        try(final InputStream stream = new GZIPInputStream(this.getClass().getResourceAsStream(FILE))) {
            Assert.assertNotNull(stream);
            final BufferedReader reader = new BufferedReader(Channels.newReader(Channels.newChannel(stream), "UTF-8"));
            final List<Callable<String[]>> lines = new ArrayList<>();
            int nline = 0;
            String line = null;
            for(; (line = reader.readLine()) != null; nline++) {
                if (nline == 0) {
                    continue; // skips header
                }
                lines.add(this.checkLineTokenizationMatchesPromise(line));
            }        
            List<Future<String[]>> all = Executors.newWorkStealingPool().invokeAll(lines);
            for (Future<String[]> future : all) {
                final String[] result = future.get();
                assertEquals(result[0], result[1]);
            }
        }
    }

}
