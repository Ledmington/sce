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
package com.ledmington;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.StandardCharsets;

import com.ledmington.sce.Engine;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.tokens.Token;
import com.ledmington.sce.tokens.Tokenizer;

public final class Main {

    private static final PrintWriter out = System.console() == null
            ? new PrintWriter(System.out, false, StandardCharsets.UTF_8)
            : System.console().writer();

    public static void main(final String[] args) {
        final String input = String.join(" ", args);
        final Token[] tokens = Tokenizer.tokenize(input);
        Node current = Parser.parse(tokens);
        out.printf("Input: %s%n", current.toExpression());
        Node next = Engine.simplify(current);

        int iteration = 0;
        while (!current.equals(next)) {
            out.printf(" %2d: %s%n", iteration, next.toExpression());
            current = next;
            next = Engine.simplify(current);
            iteration++;
        }

        out.printf("Final result: %s%n", next.toExpression());
        if (next instanceof FractionNode fn
                && fn.numerator() instanceof ConstantNode num
                && fn.denominator() instanceof ConstantNode den) {
            out.printf(
                    "Value: %.20f%n",
                    new BigDecimal(num.value()).divide(new BigDecimal(den.value()), new MathContext(20)));
        }
        out.printf("Final result (LaTeX): %s%n", next.toLatex());

        out.flush();
    }
}
