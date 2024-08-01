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

import java.math.BigInteger;

public record ConstantNode(BigInteger value) implements Node {
    public static ConstantNode of(final int value) {
        return new ConstantNode(BigInteger.valueOf(value));
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String toExpression() {
        return value.toString();
    }

    @Override
    public String toLatex() {
        return value.toString();
    }
}
