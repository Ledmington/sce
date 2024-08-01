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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MultiplyNode implements MultiNode {

    private final List<Node> children;

    public MultiplyNode(final Node... children) {
        this.children = new ArrayList<>(children.length);
        for (final Node n : children) {
            this.children.add(Objects.requireNonNull(n));
        }
    }

    public MultiplyNode(final List<Node> nodes) {
        this.children = new ArrayList<>(Objects.requireNonNull(nodes).size());
        for (final Node n : nodes) {
            this.children.add(Objects.requireNonNull(n));
        }
    }

    @Override
    public ConstantNode identity() {
        return ConstantNode.of(1);
    }

    @Override
    public int numChildren() {
        return children.size();
    }

    @Override
    public Node getChild(final int idx) {
        return children.get(idx);
    }

    @Override
    public boolean isConstant() {
        return children.stream().allMatch(Node::isConstant);
    }

    @Override
    public int size() {
        return 1 + children.stream().mapToInt(Node::size).sum();
    }

    @Override
    public String toExpression() {
        return children.stream().map(Node::toExpression).collect(Collectors.joining("*"));
    }

    @Override
    public String toLatex() {
        return children.stream().map(Node::toLatex).collect(Collectors.joining(" \\cdot "));
    }

    @Override
    public String toString() {
        return "MultiplyNode(children=" + children + ")";
    }

    @Override
    public int hashCode() {
        return 17 + 31 * children.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        return this.children.equals(((MultiplyNode) other).children);
    }
}
