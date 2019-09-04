package ai.vitk.tok;

import ai.vitk.type.Token;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.util.stream.Collectors;

public class TokenizeInput {

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            final Tokenizer tokenizer = new Tokenizer();
            final PrintStream output = System.out;
            try(final InputStream stream = new FileInputStream(args[0])) {
                final BufferedReader reader = new BufferedReader(Channels.newReader(Channels.newChannel(stream), "UTF-8"));
                String line = null;
                int nline = 0;
                for(; (line = reader.readLine()) != null; nline++) {
                    if (nline == 0 || line.length() == 0) {
                        output.println(line);
                    } else {
                        final String[] parts = line.split("\\t");
                        final String text = parts[0];
                        final String tokenized = tokenizer.tokenize(text).stream()
                            .map(Token::getWord)
                            .map(s -> s.replaceAll("\\s+", "_"))
                            .collect(Collectors.joining(" "));
                        output.print(text);
                        output.print('\t');
                        output.println(tokenized);
                    }
                    output.flush();
                }
            }
        }
    }

}
