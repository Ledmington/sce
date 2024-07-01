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

import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.tokens.Tokenizer;

final class TestEngine {

    private static Stream<Arguments> correctSimplifications() {
        final ConstantNode one = new ConstantNode(BigInteger.ONE);
        final ConstantNode two = new ConstantNode(BigInteger.TWO);
        final ConstantNode three = new ConstantNode(BigInteger.valueOf(3));
        final ConstantNode four = new ConstantNode(BigInteger.valueOf(4));
        final ConstantNode five = new ConstantNode(BigInteger.valueOf(5));
        final ConstantNode six = new ConstantNode(BigInteger.valueOf(6));
        final FractionNode oneHalf = new FractionNode(one, two);
        final FractionNode twoThirds = new FractionNode(two, three);
        final FractionNode sevenThirds = new FractionNode(new ConstantNode(BigInteger.valueOf(7)), three);
        return Stream.of(
                Arguments.of("1", one),
                Arguments.of("(1)", one),
                Arguments.of("1+2", three),
                Arguments.of("1-2", new ConstantNode(BigInteger.valueOf(-1))),
                Arguments.of("1*2", two),
                Arguments.of("2+3*4", new ConstantNode(BigInteger.valueOf(14))),
                Arguments.of("(2+3)*4", new ConstantNode(BigInteger.valueOf(20))),
                // fractions are not resolved
                Arguments.of("1/2", oneHalf),
                Arguments.of("(1/2)", oneHalf),
                // fractions can be simplified
                Arguments.of("-2/-3", twoThirds),
                Arguments.of("6/8", new FractionNode(three, four)),
                // "by convention", we choose to always have the sign of a fraction at the numerator
                Arguments.of("2/-3", new FractionNode(new ConstantNode(BigInteger.valueOf(-2)), three)),
                // 1/2+1/3 = 5/6
                Arguments.of("1/2+1/3", new FractionNode(five, six)),
                // 2+1/3 = 7/3
                Arguments.of("2+1/3", sevenThirds),
                // 1/3+2 = 7/3
                Arguments.of("1/3+2", sevenThirds),
                // 5/6-1/3 = 1/2
                Arguments.of("5/6-1/3", oneHalf),
                // 2/3*4/5 = 8/15
                Arguments.of(
                        "2/3*4/5",
                        new FractionNode(
                                new ConstantNode(BigInteger.valueOf(8)), new ConstantNode(BigInteger.valueOf(15)))),
                // (2/3)/4 = 1/6
                Arguments.of("(2/3)/4", new FractionNode(one, six)),
                // 2/(3/4) = 8/3
                Arguments.of("2/(3/4)", new FractionNode(new ConstantNode(BigInteger.valueOf(8)), three)),
                // (2/3)/(4/5) = 5/6
                Arguments.of("(2/3)/(4/5)", new FractionNode(five, six)),
                // 1+(2/3)+2 = 3+(2/3)
                Arguments.of("1+(2/3)+2", new PlusNode(three, twoThirds)));
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
