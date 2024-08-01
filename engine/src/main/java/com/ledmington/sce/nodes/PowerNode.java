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

public record PowerNode(Node base, Node exponent) implements Node {
    public static PowerNode of(final int constant) {
        return new PowerNode(ConstantNode.of(constant), ConstantNode.of(1));
    }

    public static PowerNode of(final int numerator, final int denominator) {
        return new PowerNode(ConstantNode.of(numerator), ConstantNode.of(denominator));
    }

    @Override
    public boolean isConstant() {
        return base.isConstant() && exponent.isConstant();
    }

    @Override
    public int size() {
        return 1 + base.size() + exponent.size();
    }

    @Override
    public String toExpression() {
        return ((base instanceof ConstantNode || base instanceof VariableNode)
                        ? base.toExpression()
                        : ("(" + base.toExpression() + ")"))
                + "^"
                + ((exponent instanceof ConstantNode || exponent instanceof VariableNode)
                        ? exponent.toExpression()
                        : ("(" + exponent.toExpression() + ")"));
    }

    @Override
    public String toLatex() {
        return base.toLatex() + "^" + exponent.toLatex();
    }
}
