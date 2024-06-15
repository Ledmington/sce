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
package com.ledmington.sce.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ledmington.sce.tokens.IntegerLiteral;
import com.ledmington.sce.tokens.Symbols;
import com.ledmington.sce.tokens.Token;

public final class Parser {

    private Parser() {}

    public static Node parse(final Token[] input) {
        final List<Object> partialAST = new ArrayList<>(Arrays.asList(input));

        for (int i = 0; i < partialAST.size(); i++) {
            if (partialAST.get(i) instanceof IntegerLiteral il) {
                partialAST.set(i, new ConstantNode(il.value()));
            }
        }

        while (partialAST.size() > 1) {
            final int startSize = partialAST.size();
            for (int i = 0; i < partialAST.size(); i++) {
                if (i < partialAST.size() - 2
                        && partialAST.get(i) instanceof Node ln
                        && partialAST.get(i + 1) == Symbols.ASTERISK
                        && partialAST.get(i + 2) instanceof Node rn) {
                    partialAST.remove(i);
                    partialAST.remove(i);
                    partialAST.set(i, new MultiplyNode(ln, rn));
                }
            }
            for (int i = 0; i < partialAST.size(); i++) {
                if (i < partialAST.size() - 2
                        && partialAST.get(i) instanceof Node ln
                        && partialAST.get(i + 1) == Symbols.PLUS
                        && partialAST.get(i + 2) instanceof Node rn) {
                    partialAST.remove(i);
                    partialAST.remove(i);
                    partialAST.set(i, new PlusNode(ln, rn));
                }
                if (i < partialAST.size() - 2
                        && partialAST.get(i) instanceof Node ln
                        && partialAST.get(i + 1) == Symbols.MINUS
                        && partialAST.get(i + 2) instanceof Node rn) {
                    partialAST.remove(i);
                    partialAST.remove(i);
                    partialAST.set(i, new MinusNode(ln, rn));
                }
                if (i < partialAST.size() - 2
                        && partialAST.get(i) == Symbols.LEFT_BRACKET
                        && partialAST.get(i + 1) instanceof Node n
                        && partialAST.get(i + 2) == Symbols.RIGHT_BRACKET) {
                    partialAST.remove(i);
                    partialAST.remove(i);
                    partialAST.set(i, new BracketNode(n));
                }
            }

            if (partialAST.size() == startSize) {
                throw new Error(String.format(
                        "Invalid expression: '%s'",
                        partialAST.stream()
                                .map(x -> (x instanceof Node n) ? n.toExpression() : x.toString())
                                .collect(Collectors.joining(" "))));
            }
        }

        return (Node) partialAST.getFirst();
    }
}
