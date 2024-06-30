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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MinusNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.tokens.Tokenizer;

final class TestParser {

    private static Stream<Arguments> correctNodes() {
        return Stream.of(
                Arguments.of("1", new ConstantNode(BigInteger.ONE)),
                Arguments.of("(1)", new BracketNode(new ConstantNode(BigInteger.ONE))),
                Arguments.of("1+2", new PlusNode(new ConstantNode(BigInteger.ONE), new ConstantNode(BigInteger.TWO))),
                Arguments.of("1-2", new MinusNode(new ConstantNode(BigInteger.ONE), new ConstantNode(BigInteger.TWO))),
                Arguments.of(
                        "1*2", new MultiplyNode(new ConstantNode(BigInteger.ONE), new ConstantNode(BigInteger.TWO))),
                Arguments.of(
                        "1/2", new FractionNode(new ConstantNode(BigInteger.ONE), new ConstantNode(BigInteger.TWO))));
    }

    @ParameterizedTest
    @MethodSource("correctNodes")
    void parsing(final String input, final Node expected) {
        final Node actual = Parser.parse(Tokenizer.tokenize(input));
        assertEquals(expected, actual, () -> String.format("Expected '%s' but was '%s'", expected, actual));
    }
}
