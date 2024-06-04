package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Lox {
    static boolean hadError = false;

    public static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if(hadError) System.exit(64);
    }

    // run jlox code interactively
    // run jlox common without any agr, it drops you into a prompt where you can enter and execute code one line at a time.

    public static  void runPrompt() throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);

        BufferedReader reader = new BufferedReader(inputStreamReader);

        while (true){
            System.out.println(">");
            String line = reader.readLine();

            if(line == null) break;

            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(String line , String message) {
        report(line , "" , message);
    }

    public static void report(String line , String where , String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    public static void main(String[] args) throws IOException {
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
            // run programs script
        } else {
            // run prompt
            runPrompt();
        }
    }
}
