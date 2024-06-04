package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static  com.craftinginterpreters.lox.TokenType.*;

// This class is the implementation of lexical analyzer (or scanner) for the lox programing language.
// The scanner is responsible for reading the source code and converting it into a list of tokens,
// which represent the smallest uint of meaningful data (eg: operators , keywords , identifier)

//The Scanner class processes the source code character by character, identifying lexemes and converting
// them into tokens. It supports various token types such as parentheses, braces, operators,
// literals (strings and numbers), and handles comments and whitespace.
// If an unexpected character is encountered, it reports an error.
// This class is a key component in the lexical analysis phase of the compiler for the lox programming language.

public class Scanner {
    // the source code to be scan
    private final String source;

    // a list of store and generated tokens
    private final List<Token> tokens  = new ArrayList<>();

    // the start position of the current lexeme
    private int start = 0;

    // the current position in the source
    private int current = 0;

    // the current line number in the source code
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    // determines the type of token base on the current character and calls
    // the appropriate methods to handle it.

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG);
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL );
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '<': addToken(match('=') ? LESS_EQUAL : LESS);
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // ignore whitespace
                break;
            case '\n':
                // ignore new line
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if(isDigit(c)) {
                    number();
                } else {
                    Lox.error(Integer.toString(line), "unexpected character!");
                }
            break;
        }
    }

    // return the current character without consuming it
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

   // consume the next character if it matches the expected character
    private boolean match(char excepted) {
        if(isAtEnd()) return false;

        if(source.charAt(current) != excepted) return  false;

        current++;
        return true;
    };

    // return the current character
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(Integer.toString(line), "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // handle number literals including functional part
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
}
