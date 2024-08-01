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

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.nodes.PowerNode;
import com.ledmington.sce.nodes.VariableNode;
import com.ledmington.sce.tokens.Tokenizer;

final class TestParser {

    private static Stream<Arguments> correctNodes() {
        final ConstantNode one = ConstantNode.of(1);
        final ConstantNode two = ConstantNode.of(2);
        final VariableNode x = new VariableNode("x");
        return Stream.of(
                Arguments.of("1", ConstantNode.of(1)),
                Arguments.of("-1", ConstantNode.of(-1)),
                Arguments.of("(1)", new BracketNode(one)),
                Arguments.of("1+2", new PlusNode(one, two)),
                Arguments.of("1-2", new PlusNode(one, new MultiplyNode(ConstantNode.of(-1), two))),
                Arguments.of("1*2", new MultiplyNode(one, two)),
                Arguments.of("1/2", FractionNode.of(1, 2)),
                Arguments.of("1^2", PowerNode.of(1, 2)),
                Arguments.of("1+x", new PlusNode(one, x)),
                Arguments.of("1-x", new PlusNode(one, new MultiplyNode(ConstantNode.of(-1), x))),
                Arguments.of("1*x", new MultiplyNode(one, x)),
                Arguments.of("1/x", new FractionNode(one, x)),
                Arguments.of("1^x", new PowerNode(one, x)));
    }

    @ParameterizedTest
    @MethodSource("correctNodes")
    void parsing(final String input, final Node expected) {
        final Node actual = Parser.parse(Tokenizer.tokenize(input));
        assertEquals(expected, actual, () -> String.format("Expected '%s' but was '%s'", expected, actual));
    }
}
