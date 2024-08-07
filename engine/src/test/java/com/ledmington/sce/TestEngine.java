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

import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.nodes.VariableNode;
import com.ledmington.sce.tokens.Tokenizer;

final class TestEngine {

    private static Stream<Arguments> correctSimplifications() {
        final ConstantNode one = ConstantNode.of(1);
        final ConstantNode two = ConstantNode.of(2);
        final ConstantNode three = ConstantNode.of(3);
        final FractionNode oneHalf = FractionNode.of(1, 2);
        final FractionNode twoThirds = FractionNode.of(2, 3);
        final FractionNode sevenThirds = FractionNode.of(7, 3);
        return Stream.of(
                Arguments.of("1", one),
                Arguments.of("(1)", one),
                Arguments.of("1+2", three),
                Arguments.of("1-2", ConstantNode.of(-1)),
                Arguments.of("1*2", two),
                Arguments.of("2+3*4", ConstantNode.of(14)),
                Arguments.of("(2+3)*4", ConstantNode.of(20)),
                // fractions are not resolved
                Arguments.of("1/2", oneHalf),
                Arguments.of("(1/2)", oneHalf),
                // fractions can be simplified
                Arguments.of("-2/-3", twoThirds),
                Arguments.of("6/8", FractionNode.of(3, 4)),
                // fractions with denominator 1 are converted to constants
                Arguments.of("5/1", ConstantNode.of(5)),
                // "by convention", we choose to always have the sign of a fraction at the
                // numerator
                Arguments.of("2/-3", FractionNode.of(-2, 3)),
                // 1/2+1/3 = 5/6
                Arguments.of("1/2+1/3", FractionNode.of(5, 6)),
                // 2+1/3 = 7/3
                Arguments.of("2+1/3", sevenThirds),
                // 1/3+2 = 7/3
                Arguments.of("1/3+2", sevenThirds),
                // 5/6-1/3 = 1/2
                Arguments.of("5/6-1/3", oneHalf),
                // 2/3*4/5 = 8/15
                Arguments.of("2/3*4/5", FractionNode.of(8, 15)),
                // (2/3)/4 = 1/6
                Arguments.of("(2/3)/4", FractionNode.of(1, 6)),
                // 2/(3/4) = 8/3
                Arguments.of("2/(3/4)", FractionNode.of(8, 3)),
                // (2/3)/(4/5) = 5/6
                Arguments.of("(2/3)/(4/5)", FractionNode.of(5, 6)),
                // 1^2 = 1
                Arguments.of("1^2", one),
                // 2^1 = 2
                Arguments.of("2^1", two),
                // 2^3 = 8
                Arguments.of("2^3", ConstantNode.of(8)),
                // (2/3)^3 = 8/27
                Arguments.of("(2/3)^3", FractionNode.of(8, 27)),
                // x = x
                Arguments.of("x", new VariableNode("x")),
                // (x) = x
                Arguments.of("(x)", new VariableNode("x")),
                // x+1 = x+1
                Arguments.of("x+1", new PlusNode(new VariableNode("x"), one)),
                // x+1+2 = x+3
                Arguments.of("x+1+2", new PlusNode(new VariableNode("x"), three)),
                // 1+x+2 = 3+x
                Arguments.of("1+x+2", new PlusNode(three, new VariableNode("x"))),
                // x*2 = x*2
                Arguments.of("x*2", new MultiplyNode(new VariableNode("x"), two)),
                // x*2*3 = x*6
                Arguments.of("x*2*3", new MultiplyNode(new VariableNode("x"), ConstantNode.of(6))),
                // 2*x*3 = 6*x
                Arguments.of("2*x*3", new MultiplyNode(ConstantNode.of(6), new VariableNode("x"))),
                // x/2 = x/2
                Arguments.of("x/2", new FractionNode(new VariableNode("x"), two)),
                // 2*(3/x) = 6/x
                Arguments.of("2*(3/x)", new FractionNode(ConstantNode.of(6), new VariableNode("x"))),
                // i*i = -1
                Arguments.of("i*i", ConstantNode.of(-1)),
                // i^2 = -1
                Arguments.of("i^2", ConstantNode.of(-1)),
                // i^3 = -i
                Arguments.of("i^3", new MultiplyNode(ConstantNode.of(-1), EngineConstants.getImaginaryUnit())),
                // i^4 = 1
                Arguments.of("i^4", ConstantNode.of(1)),
                // i^5 = i
                Arguments.of("i^5", EngineConstants.getImaginaryUnit()));
    }

    @ParameterizedTest
    @MethodSource("correctSimplifications")
    void solving(final String input, final Node expected) {
        Node current = Parser.parse(Tokenizer.tokenize(input));
        Node next = Engine.simplify(current);
        while (!current.equals(next)) {
            current = next;
            next = Engine.simplify(current);
        }
        final Node finalNext = next;
        assertEquals(
                expected,
                next,
                () -> String.format("Expected '%s' but was '%s'", expected.toExpression(), finalNext.toExpression()));
    }
}
