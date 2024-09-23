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
import java.util.List;
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
import com.ledmington.sce.nodes.PowerNode;
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
        final VariableNode x = new VariableNode("x");
        return Stream.of(
                // constants
                Arguments.of("1", one),
                Arguments.of("(1)", one),
                Arguments.of("1+2", three),
                Arguments.of("1-2", ConstantNode.of(-1)),
                Arguments.of("1*2", two),
                Arguments.of("2+3*4", ConstantNode.of(14)),
                Arguments.of("(2+3)*4", ConstantNode.of(20)),
                Arguments.of("1^2", one),
                Arguments.of("2^1", two),
                Arguments.of("0^2", ConstantNode.of(0)),
                Arguments.of("2^0", ConstantNode.of(1)),
                Arguments.of("3^3^3", ConstantNode.of(19_683)),
                Arguments.of("3^(3^3)", new ConstantNode(BigInteger.valueOf(7_625_597_484_987L))),
                // fractions
                Arguments.of("1/2", oneHalf),
                Arguments.of("(1/2)", oneHalf),
                Arguments.of("-2/-3", twoThirds),
                Arguments.of("6/8", FractionNode.of(3, 4)),
                Arguments.of("5/1", ConstantNode.of(5)),
                Arguments.of("2/-3", FractionNode.of(-2, 3)),
                Arguments.of("1/2+1/3", FractionNode.of(5, 6)),
                Arguments.of("2+1/3", sevenThirds),
                Arguments.of("1/3+2", sevenThirds),
                Arguments.of("5/6-1/3", oneHalf),
                Arguments.of("2/3*4/5", FractionNode.of(8, 15)),
                Arguments.of("(2/3)/4", FractionNode.of(1, 6)),
                Arguments.of("2/(3/4)", FractionNode.of(8, 3)),
                Arguments.of("(2/3)/(4/5)", FractionNode.of(5, 6)),
                Arguments.of("1^2", one),
                Arguments.of("2^1", two),
                Arguments.of("2^3", ConstantNode.of(8)),
                Arguments.of("(2/3)^3", FractionNode.of(8, 27)),
                //
                Arguments.of("x", x),
                Arguments.of("(x)", x),
                Arguments.of("x+0", x),
                Arguments.of("0+x", x),
                Arguments.of("0+x+0", x),
                Arguments.of("x+0+0", x),
                Arguments.of("0+0+x", x),
                Arguments.of("x+1", new PlusNode(x, one)),
                Arguments.of("x+1+2", new PlusNode(x, three)),
                Arguments.of("1+x+2", new PlusNode(three, x)),
                Arguments.of("x*1", x),
                Arguments.of("1*x", x),
                Arguments.of("1*x*1", x),
                Arguments.of("x*1*1", x),
                Arguments.of("1*1*x", x),
                Arguments.of("x*2", new MultiplyNode(x, two)),
                Arguments.of("x*2*3", new MultiplyNode(x, ConstantNode.of(6))),
                Arguments.of("2*x*3", new MultiplyNode(ConstantNode.of(6), x)),
                Arguments.of("x/2", new FractionNode(x, two)),
                Arguments.of("2*(3/x)", new FractionNode(ConstantNode.of(6), x)),
                Arguments.of(
                        "x+3+x",
                        new PlusNode(List.of(new MultiplyNode(List.of(ConstantNode.of(2), x)), ConstantNode.of(3)))),
                Arguments.of(
                        "x*3*x", new MultiplyNode(List.of(new PowerNode(x, ConstantNode.of(2)), ConstantNode.of(3)))),
                // imaginary unit
                Arguments.of("i*i", ConstantNode.of(-1)),
                Arguments.of("i^2", ConstantNode.of(-1)),
                Arguments.of("i^3", new MultiplyNode(ConstantNode.of(-1), EngineConstants.getImaginaryUnit())),
                Arguments.of("i^4", ConstantNode.of(1)),
                Arguments.of("i^5", EngineConstants.getImaginaryUnit()),
                Arguments.of("i^6", ConstantNode.of(-1)),
                Arguments.of("i^7", new MultiplyNode(ConstantNode.of(-1), EngineConstants.getImaginaryUnit())),
                Arguments.of("i^8", ConstantNode.of(1)));
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
