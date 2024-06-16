/*
* sce - Symbolic Calculus Engine
* Copyright (C) 2024-2024 Filippo Barbari <filippo.barbari@gmail.com>
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.ledmington.sce.tokens;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {

    private static char[] v;
    private static int i;

    private Tokenizer() {}

    public static Token[] tokenize(final String input) {
        final List<Token> tokens = new ArrayList<>();
        v = input.toCharArray();
        i = 0;
        while (i < v.length) {
            final char ch = v[i];
            switch (ch) {
                case '(':
                    tokens.add(Symbols.LEFT_BRACKET);
                    i++;
                    break;
                case ')':
                    tokens.add(Symbols.RIGHT_BRACKET);
                    i++;
                    break;
                case '+':
                    tokens.add(Symbols.PLUS);
                    i++;
                    break;
                case '-':
                    tokens.add(Symbols.MINUS);
                    i++;
                    break;
                case '*':
                    tokens.add(Symbols.ASTERISK);
                    i++;
                    break;
                default:
                    if (Character.isDigit(ch)) {
                        tokens.add(readIntegerLiteral());
                    } else {
                        throw new Error(String.format("Unknown character '%c'", ch));
                    }
            }
        }
        return tokens.toArray(new Token[0]);
    }

    private static IntegerLiteral readIntegerLiteral() {
        final StringBuilder sb = new StringBuilder(10);
        while (i < v.length && Character.isDigit(v[i])) {
            sb.append(v[i]);
            i++;
        }
        return new IntegerLiteral(new BigInteger(sb.toString(), 10));
    }
}
