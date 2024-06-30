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

import com.ledmington.sce.Engine;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.Parser;
import com.ledmington.sce.tokens.Token;
import com.ledmington.sce.tokens.Tokenizer;

public final class Main {

    public static void main(final String[] args) {
        final String input = String.join(" ", args);
        final Token[] tokens = Tokenizer.tokenize(input);
        Node current = Parser.parse(tokens);
        System.out.println(current.toExpression());
        Node next = Engine.simplify(current);
        System.out.println(next.toExpression());
        while (!current.equals(next)) {
            current = next;
            next = Engine.simplify(current);
            System.out.println(next.toExpression());
        }
    }
}
