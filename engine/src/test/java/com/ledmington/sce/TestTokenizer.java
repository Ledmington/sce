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
package com.ledmington.sce;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.math.BigInteger;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ledmington.sce.tokens.IntegerLiteral;
import com.ledmington.sce.tokens.Symbols;
import com.ledmington.sce.tokens.Token;
import com.ledmington.sce.tokens.Tokenizer;

public class TestTokenizer {

    private static Stream<Arguments> correctTokens() {
        return Stream.of(
                Arguments.of("(", Symbols.LEFT_BRACKET),
                Arguments.of(")", Symbols.RIGHT_BRACKET),
                Arguments.of("+", Symbols.PLUS),
                Arguments.of("-", Symbols.MINUS),
                Arguments.of("*", Symbols.ASTERISK),
                Arguments.of("0", new IntegerLiteral(BigInteger.ZERO)),
                Arguments.of("1", new IntegerLiteral(BigInteger.ONE)),
                Arguments.of("2", new IntegerLiteral(BigInteger.TWO)),
                Arguments.of("10", new IntegerLiteral(BigInteger.TEN)));
    }

    @ParameterizedTest
    @MethodSource("correctTokens")
    void tokenizing(final String input, final Token expected) {
        assertArrayEquals(new Token[] {expected}, Tokenizer.tokenize(input));
    }
}
