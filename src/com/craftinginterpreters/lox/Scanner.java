package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static  com.craftinginterpreters.lox.TokenType.*;

// scanner class for lexical analysis
// part of the compiler's front end, responsible for breaking down the input source code into tokens

public class Scanner {
    private final String source;
    private final List<Token> tokens  = new ArrayList<>();
    private int start = 0;
    private int current = 0;
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
            default: Lox.error(Integer.toString(line), "unexpected character!");
        }
    }

    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char excepted) {
        if(isAtEnd())return false;

        if(source.charAt(current) != excepted) return  false;

        current++;
        return true;
    };

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
}
