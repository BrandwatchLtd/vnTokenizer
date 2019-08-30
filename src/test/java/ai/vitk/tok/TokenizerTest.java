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
        checkTokenizationFormatted(
            "Bà Hồ Thị Kim Thoa nghỉ hưu từ 1/9,CPI tháng 8 tăng 0,92% Bà Hồ Thị Kim Thoa nghỉ hưu từ 1/9, CPI tháng 8 tăng 0,92% , cơn sốt nhập \"trâu cày\" Bitcoin, Bão Harvey tác động nặng nề kinh tế Mỹ, Tổng giám đốc Habeco bị \"truất\" quyền điều hành",
            "Bà, Hồ Thị Kim Thoa, nghỉ, hưu, từ, 1/9, CPI, tháng, 8, tăng, 0,92%, Bà, Hồ Thị Kim Thoa, nghỉ, hưu, từ, 1/9, CPI, tháng, 8, tăng, 0,92%, cơn sốt, nhập, trâu, cày, Bitcoin, Bão Harvey, tác động, nặng nề, kinh tế, Mỹ, Tổng giám đốc, Habeco, bị, truất, quyền, điều hành"
        );
        checkTokenizationFormatted(
            "Anh chính thức khởi động tiến trình Brexit Anh chính thức khởi động tiến trình Brexit; Tổng thống bị phế truất Park Guen-hye bị bắt; Bé gái người Việt bị sát hại ở Nhật và nỗi lo nơi xứ người; Trump bỏ chính sách xanh của Obama; Những vấn đề gai góc trên bàn nghị sự Trump - Tập; Bộ ngoại giao Việt Nam lên tiếng vụ bé gái Việt bị sát hại tại Nhật; Xả súng tại hộp đêm ở Mỹ; Chân dung Trưởng Đặc khu HonHãng bay đồng loạt tăng phí,giá điện sắp tăng sau 2 năm đứng im",
            "Anh, chính thức, khởi động, tiến trình, Brexit Anh, chính thức, khởi động, tiến trình, Brexit, Tổng thống, bị, phế truất, Park Guen, hye, bị, bắt, Bé, gái, người, Việt, bị, sát hại, ở, Nhật, và, nỗi, lo, nơi, xứ, người, Trump, bỏ, chính sách, xanh, của, Obama, Những, vấn đề, gai góc, trên, bàn, nghị sự, Trump, Tập, Bộ, ngoại giao, Việt Nam, lên tiếng, vụ, bé, gái, Việt, bị, sát hại, tại, Nhật, Xả, súng, tại, hộp đêm, ở, Mỹ, Chân dung, Trưởng Đặc khu, HonHãng, bay, đồng loạt, tăng, phí, giá, điện, sắp, tăng, sau, 2, năm, đứng, im"
        );
        checkTokenizationFormatted(
            " HÀNG RÀO PHÂN CÁCH INOX - CỘT CHẮN INOX- Hàng rào phân cách inox trắng- Hàng rào phân cách inox vàng- Hàng rào phân cách inox trắng dây chắn căng xanh, đỏ- Cột chắn di động inox vàng- Cột chắn di động inox trắng dây chắn căng xanh, đỏ- Cột chắn di động inox vàng dây chắn căng xanh, đỏ- Hàng rào inox, cột phân luồng, dải phân cách mềm, cột inox, cột chắn đường điTAG:thung rac, thùng rác, cot chan, cot chan inox, cột chăn inox, bien bang menu, bảng menu, bang welcome, sọt rac, dung cu, thiet bi ve sinh, cay, cay lau, cây lau nhà, thùng rac đá hoa cương, thung rác inox, thùng rác nhựa, cột chắn dây nhung, cột chắn dây căng, xe dọn phòng, thùng rác 240L, thùng rac composite, bien bao đang làm vệ sinh, biển báo chống trơn trượt, xe lau nhà, bộ gạt kính, cây lau kính, cây lau nhà giá",
            "HÀNG, RÀO, PHÂN, CÁCH, INOX, CỘT, CHẮN, INOX, Hàng rào, phân cách, inox, trắng, Hàng rào, phân cách, inox, vàng, Hàng rào, phân cách, inox, trắng, dây, chắn, căng, xanh, đỏ, Cột, chắn, di động, inox, vàng, Cột, chắn, di động, inox, trắng, dây, chắn, căng, xanh, đỏ, Cột, chắn, di động, inox, vàng, dây, chắn, căng, xanh, đỏ, Hàng rào, inox, cột, phân luồng, dải phân cách, mềm, cột, inox, cột, chắn, đường, điTAG, thung, rac, thùng, rác, cot, chan, cot, chan, inox, cột, chăn, inox, bien, bang, menu, bảng, menu, bang, welcome, sọt, rac, dung, cu, thiet, bi ve, sinh, cay, cay, lau, cây lau nhà, thùng, rac, đá hoa cương, thung, rác, inox, thùng, rác, nhựa, cột, chắn, dây, nhung, cột, chắn, dây, căng, xe, dọn, phòng, thùng, rác, 240L, thùng, rac, composite, bien, bao, đang, làm, vệ sinh, biển báo, chống, trơn, trượt, xe, lau, nhà, bộ, gạt, kính, cây, lau, kính, cây lau nhà, giá"
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

    private void checkTokenizationFormatted(String text, String formatted) {
        assertTrue(checkTokenizationMatchesFormatted(text, formatted));
    }
    
    private void checkTokenization(String text, String... expectedTokens) {
        assertTrue(checkTokenizationMatches(text, expectedTokens));
    }

    private boolean checkTokenizationMatchesFormatted(String text, String formatted) {
        return checkTokenizationMatches(
            text, 
            Arrays.asList(formatted.split(",\\s+")).stream().map(String::trim).filter(s -> s.length() > 0).toArray(String[]::new)
        );
    }
    
    private boolean checkTokenizationMatches(String text, String... expectedTokens) {
        List<Token> tokens = tokenizer.tokenize(text);
        List<String> actual = tokens.stream()
                .map(Token::getWord)
                .map(String::trim)
                .filter(s -> s.length() > 1 || !s.matches("['\",;\\-:]")) // TODO review why the tokenizer is not removing these
                .collect(Collectors.toList());
        return Arrays.asList(expectedTokens).equals(actual);
    }

    private Callable<Boolean> checkTokenizationMatchesPromise(int idx) {
        switch (idx % 5) {
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
                return () -> this.checkTokenizationMatchesFormatted(
                    "Anh chính thức khởi động tiến trình Brexit Anh chính thức khởi động tiến trình Brexit; Tổng thống bị phế truất Park Guen-hye bị bắt; Bé gái người Việt bị sát hại ở Nhật và nỗi lo nơi xứ người; Trump bỏ chính sách xanh của Obama; Những vấn đề gai góc trên bàn nghị sự Trump - Tập; Bộ ngoại giao Việt Nam lên tiếng vụ bé gái Việt bị sát hại tại Nhật; Xả súng tại hộp đêm ở Mỹ; Chân dung Trưởng Đặc khu HonHãng bay đồng loạt tăng phí,giá điện sắp tăng sau 2 năm đứng im",
                    "Anh, chính thức, khởi động, tiến trình, Brexit Anh, chính thức, khởi động, tiến trình, Brexit, Tổng thống, bị, phế truất, Park Guen, hye, bị, bắt, Bé, gái, người, Việt, bị, sát hại, ở, Nhật, và, nỗi, lo, nơi, xứ, người, Trump, bỏ, chính sách, xanh, của, Obama, Những, vấn đề, gai góc, trên, bàn, nghị sự, Trump, Tập, Bộ, ngoại giao, Việt Nam, lên tiếng, vụ, bé, gái, Việt, bị, sát hại, tại, Nhật, Xả, súng, tại, hộp đêm, ở, Mỹ, Chân dung, Trưởng Đặc khu, HonHãng, bay, đồng loạt, tăng, phí, giá, điện, sắp, tăng, sau, 2, năm, đứng, im"
                    );
            case 3:
                return () -> this.checkTokenizationMatchesFormatted(
                    " HÀNG RÀO PHÂN CÁCH INOX - CỘT CHẮN INOX- Hàng rào phân cách inox trắng- Hàng rào phân cách inox vàng- Hàng rào phân cách inox trắng dây chắn căng xanh, đỏ- Cột chắn di động inox vàng- Cột chắn di động inox trắng dây chắn căng xanh, đỏ- Cột chắn di động inox vàng dây chắn căng xanh, đỏ- Hàng rào inox, cột phân luồng, dải phân cách mềm, cột inox, cột chắn đường điTAG:thung rac, thùng rác, cot chan, cot chan inox, cột chăn inox, bien bang menu, bảng menu, bang welcome, sọt rac, dung cu, thiet bi ve sinh, cay, cay lau, cây lau nhà, thùng rac đá hoa cương, thung rác inox, thùng rác nhựa, cột chắn dây nhung, cột chắn dây căng, xe dọn phòng, thùng rác 240L, thùng rac composite, bien bao đang làm vệ sinh, biển báo chống trơn trượt, xe lau nhà, bộ gạt kính, cây lau kính, cây lau nhà giá",
                    "HÀNG, RÀO, PHÂN, CÁCH, INOX, CỘT, CHẮN, INOX, Hàng rào, phân cách, inox, trắng, Hàng rào, phân cách, inox, vàng, Hàng rào, phân cách, inox, trắng, dây, chắn, căng, xanh, đỏ, Cột, chắn, di động, inox, vàng, Cột, chắn, di động, inox, trắng, dây, chắn, căng, xanh, đỏ, Cột, chắn, di động, inox, vàng, dây, chắn, căng, xanh, đỏ, Hàng rào, inox, cột, phân luồng, dải phân cách, mềm, cột, inox, cột, chắn, đường, điTAG, thung, rac, thùng, rác, cot, chan, cot, chan, inox, cột, chăn, inox, bien, bang, menu, bảng, menu, bang, welcome, sọt, rac, dung, cu, thiet, bi ve, sinh, cay, cay, lau, cây lau nhà, thùng, rac, đá hoa cương, thung, rác, inox, thùng, rác, nhựa, cột, chắn, dây, nhung, cột, chắn, dây, căng, xe, dọn, phòng, thùng, rác, 240L, thùng, rac, composite, bien, bao, đang, làm, vệ sinh, biển báo, chống, trơn, trượt, xe, lau, nhà, bộ, gạt, kính, cây, lau, kính, cây lau nhà, giá"
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
        List<Callable<Boolean>> r = IntStream.range(0, 10000).mapToObj(this::checkTokenizationMatchesPromise).collect(Collectors.toList());
        List<Future<Boolean>> all = Executors.newFixedThreadPool(100).invokeAll(r);
        for (Future<Boolean> future : all) {
            assertTrue(future.get());
        }
    }

}
