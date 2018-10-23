package ai.vitk.tok;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import ai.vitk.type.Token;

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

    private void checkTokenization(String text, String... expectedTokens) {
        List<Token> tokens = tokenizer.tokenize(text);
        List<String> actual = tokens.stream()
                .map(Token::getWord)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(expectedTokens), actual);
    }

}
