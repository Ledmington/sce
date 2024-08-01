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

public record FractionNode(Node numerator, Node denominator) implements Node {
    public static FractionNode of(final int constant) {
        return new FractionNode(ConstantNode.of(constant), ConstantNode.of(1));
    }

    public static FractionNode of(final int numerator, final int denominator) {
        return new FractionNode(ConstantNode.of(numerator), ConstantNode.of(denominator));
    }

    @Override
    public boolean isConstant() {
        return numerator.isConstant() && denominator.isConstant();
    }

    @Override
    public int size() {
        return 1 + numerator.size() + denominator.size();
    }

    @Override
    public String toExpression() {
        return ((numerator instanceof ConstantNode || numerator instanceof VariableNode)
                        ? numerator.toExpression()
                        : ("(" + numerator.toExpression() + ")"))
                + "/"
                + ((denominator instanceof ConstantNode || denominator instanceof VariableNode)
                        ? denominator.toExpression()
                        : ("(" + denominator.toExpression() + ")"));
    }

    @Override
    public String toLatex() {
        return "\\frac{" + numerator.toLatex() + "}{" + denominator.toLatex() + "}";
    }
}
