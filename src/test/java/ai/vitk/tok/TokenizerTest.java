package ai.vitk.tok;

import static org.junit.Assert.assertTrue;

import ai.vitk.type.Token;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        checkTokenization(
            "Anh chính thức khởi động tiến trình Brexit Anh chính thức khởi động tiến trình Brexit; Tổng thống bị phế truất Park Guen-hye bị bắt; Bé gái người Việt bị sát hại ở Nhật và nỗi lo nơi xứ người; Trump bỏ chính sách xanh của Obama; Những vấn đề gai góc trên bàn nghị sự Trump - Tập; Bộ ngoại giao Việt Nam lên tiếng vụ bé gái Việt bị sát hại tại Nhật; Xả súng tại hộp đêm ở Mỹ; Chân dung Trưởng Đặc khu HonHãng bay đồng loạt tăng phí,giá điện sắp tăng sau 2 năm đứng im",
            "Anh, chính thức, khởi động, tiến trình, Brexit Anh, chính thức, khởi động, tiến trình, Brexit, Tổng thống, bị, phế truất, Park Guen, hye, bị, bắt, Bé, gái, người, Việt, bị, sát hại, ở, Nhật, và, nỗi, lo, nơi, xứ, người, Trump, bỏ, chính sách, xanh, của, Obama, Những, vấn đề, gai góc, trên, bàn, nghị sự, Trump, Tập, Bộ, ngoại giao, Việt Nam, lên tiếng, vụ, bé, gái, Việt, bị, sát hại, tại, Nhật, Xả, súng, tại, hộp đêm, ở, Mỹ, Chân dung, Trưởng Đặc khu, HonHãng, bay, đồng loạt, tăng, phí, giá, điện, sắp, tăng, sau, 2, năm, đứng, im".split(",")
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

    private boolean checkTokenizationMatches(String text, String... expectedTokens) {
        List<Token> tokens = tokenizer.tokenize(text);
        List<String> actual = tokens.stream()
                .map(Token::getWord)
                .collect(Collectors.toList());
        return Arrays.asList(expectedTokens).equals(actual);
    }

    private Callable<Boolean> checkTokenizationMatchesPromise(int idx) {
        switch (idx % 3) {
            case 0:
                return () -> this.checkTokenizationMatches(
                    "Direct message để được chúng mình tư vấn kỹ hơn nhé", 
                    "Direct","message", "để", "được", "chúng mình", "tư vấn", "kỹ", "hơn", "nhé"
                );
            case 1:
                return () -> this.checkTokenizationMatches(
                    "Việt Nam là quốc gia nằm ở phía Đông bán đảo Đông Dương thuộc khu vực Đông Nam Á",
                    "Việt Nam", "là", "quốc gia", "nằm", "ở", "phía", "Đông", "bán đảo", "Đông Dương", "thuộc", "khu vực", "Đông Nam", "Á"
                );
            case 2:
                return () -> this.checkTokenizationMatches(
                    "Anh chính thức khởi động tiến trình Brexit Anh chính thức khởi động tiến trình Brexit; Tổng thống bị phế truất Park Guen-hye bị bắt; Bé gái người Việt bị sát hại ở Nhật và nỗi lo nơi xứ người; Trump bỏ chính sách xanh của Obama; Những vấn đề gai góc trên bàn nghị sự Trump - Tập; Bộ ngoại giao Việt Nam lên tiếng vụ bé gái Việt bị sát hại tại Nhật; Xả súng tại hộp đêm ở Mỹ; Chân dung Trưởng Đặc khu HonHãng bay đồng loạt tăng phí,giá điện sắp tăng sau 2 năm đứng im",
                    "Anh, chính thức, khởi động, tiến trình, Brexit Anh, chính thức, khởi động, tiến trình, Brexit, Tổng thống, bị, phế truất, Park Guen, hye, bị, bắt, Bé, gái, người, Việt, bị, sát hại, ở, Nhật, và, nỗi, lo, nơi, xứ, người, Trump, bỏ, chính sách, xanh, của, Obama, Những, vấn đề, gai góc, trên, bàn, nghị sự, Trump, Tập, Bộ, ngoại giao, Việt Nam, lên tiếng, vụ, bé, gái, Việt, bị, sát hại, tại, Nhật, Xả, súng, tại, hộp đêm, ở, Mỹ, Chân dung, Trưởng Đặc khu, HonHãng, bay, đồng loạt, tăng, phí, giá, điện, sắp, tăng, sau, 2, năm, đứng, im".split(",")
                );
            default: // :-(
                return () -> this.checkTokenizationMatches(
                    "Direct message để được chúng mình tư vấn kỹ hơn nhé", 
                    "Direct","message", "để", "được", "chúng mình", "tư vấn", "kỹ", "hơn", "nhé"
                );
        }
    }
    
    @Test
    public void testMultiThreaded() throws InterruptedException, ExecutionException {
        List<Callable<Boolean>> r = IntStream.range(0, 100).mapToObj(this::checkTokenizationMatchesPromise).collect(Collectors.toList());
        List<Future<Boolean>> all = Executors.newFixedThreadPool(4).invokeAll(r);
        for (Future<Boolean> future : all) {
            assertTrue(future.get());
        }
    }

}
