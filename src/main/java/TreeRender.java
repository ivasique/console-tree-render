import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.DataFormatException;

public class TreeRender {
    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Wrong number of arguments.");

        try (BufferedReader br = Files.newBufferedReader(Paths.get(args[0]));
             BufferedWriter bw = Files.newBufferedWriter(Paths.get(args[1]))) {
            String tree = br.readLine();   // Read only first line. Ignore others.
            LinkedList<String> tokens = getTokens(tree, "(\\d+|[)( ])"); // Natural number | brackets | space.
            writeTree(bw, tokens);
        } catch (IOException e) {
            System.err.println("Error while reading/writing a file " + e.getMessage());
        }  catch (DataFormatException e) {
            System.err.println(e.getMessage());
        }
    }

    private static LinkedList<String> getTokens(String input, String regex) throws DataFormatException {
        LinkedList<String> tokens = new LinkedList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        int pos = 0;
        while (m.find()) {
            if (m.start() != pos)
                throw new DataFormatException("Invalid format of input data. Invalid token position: " + pos);
            tokens.add(m.group());
            pos = m.end();
        }
        if (pos != input.length())
            throw new DataFormatException("Invalid format of input data. Invalid token position: " + pos);
        return tokens;
    }

    private static void writeTree(BufferedWriter bw, List<String> tokens) throws DataFormatException, IOException {
        if (!tokens.get(0).equals("("))
            throw new DataFormatException("First token should be \"(\". Actually first token is " + tokens.get(0));

        String curToken;
        String prevToken;
        int level = 0;
        for (int i = 1; i < tokens.size(); i++) {
            curToken = tokens.get(i);
            prevToken = tokens.get(i-1);
            switch (curToken) {
                case "(":
                    if (!prevToken.matches("(\\)| )"))     // Right bracket | space.
                        throw new DataFormatException("Invalid order of tokens. \"" + curToken + "\" goes after \"" + prevToken + "\"");
                    level++;
                    break;
                case ")":
                    if (!prevToken.matches("(\\d+|\\))"))     // Natural number | right bracket.
                        throw new DataFormatException("Invalid order of tokens. \"" + curToken + "\" goes after \"" + prevToken + "\"");
                    if (level < 0) throw new DataFormatException("Invalid order of brackets.");
                    level--;
                    break;
                case " ":
                    if (!prevToken.matches("\\d+"))     // Natural number.
                        throw new DataFormatException("Invalid order of tokens. \"" + curToken + "\" goes after \"" + prevToken + "\"");
                    break;
                default: // natural number
                    if (!prevToken.matches("(\\(| )"))     // Left bracket | space.
                        throw new DataFormatException("Invalid order of tokens. \"" + curToken + "\" goes after \"" + prevToken + "\"");
                    try {
                        bw.write(String.format("%" + (level * 4) + "s", "")); // Hardcoded 4 is used because of task's output indent format.
                    } catch (Throwable thr) {
                        System.out.println(thr.getMessage());
                    }
                    bw.write(curToken, 0, curToken.length());
                    bw.newLine();
                    break;
            }
        }
        if (level != -1) throw new DataFormatException("Number of right and left brackets is different.");
    }
}

