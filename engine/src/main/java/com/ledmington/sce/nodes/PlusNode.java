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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class PlusNode implements MultiNode {

    private final Map<Node, Integer> children;

    public PlusNode(final Node... nodes) {
        this.children = new HashMap<>();
        for (final Node n : nodes) {
            Objects.requireNonNull(n);
            if (this.children.containsKey(n)) {
                this.children.put(n, this.children.get(n) + 1);
            } else {
                this.children.put(n, 1);
            }
        }
    }

    public PlusNode(final List<Node> nodes) {
        this.children = new HashMap<>();
        for (final Node n : nodes) {
            Objects.requireNonNull(n);
            if (this.children.containsKey(n)) {
                this.children.put(n, this.children.get(n) + 1);
            } else {
                this.children.put(n, 1);
            }
        }
    }

    @Override
    public ConstantNode identity() {
        return ConstantNode.of(0);
    }

    @Override
    public int getNumChildren() {
        return children.values().stream().mapToInt(x -> x).sum();
    }

    @Override
    public Node getChild(final int idx) {
        return children.entrySet().stream()
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .skip(idx)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public boolean isConstant() {
        return children.keySet().stream().allMatch(Node::isConstant);
    }

    @Override
    public int size() {
        return 1
                + children.entrySet().stream()
                        .mapToInt(e -> e.getValue() * e.getKey().size())
                        .sum();
    }

    @Override
    public String toExpression() {
        return children.entrySet().stream()
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .map(Node::toExpression)
                .collect(Collectors.joining("+"));
    }

    @Override
    public String toLatex() {
        return children.entrySet().stream()
                .flatMap(e -> Collections.nCopies(e.getValue(), e.getKey()).stream())
                .map(Node::toLatex)
                .collect(Collectors.joining("+"));
    }

    @Override
    public String toString() {
        return "PlusNode(children=" + children + ")";
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
        return this.children.equals(((PlusNode) other).children);
    }
}
