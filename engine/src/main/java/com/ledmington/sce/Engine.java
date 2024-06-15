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

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.MinusNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.PlusNode;

public final class Engine {

    private Engine() {}

    public static Node simplify(final Node root) {
        switch (root) {
            case BracketNode bn -> {
                // (2) -> 2
                if (bn.inner() instanceof ConstantNode cn) {
                    return cn;
                } else {
                    return new BracketNode(simplify(bn.inner()));
                }
            }
            case PlusNode pn -> {
                // 1+2 -> 3
                if (pn.lhs() instanceof ConstantNode lc && pn.rhs() instanceof ConstantNode rc) {
                    return new ConstantNode(lc.value().add(rc.value()));
                } else {
                    return new PlusNode(simplify(pn.lhs()), simplify(pn.rhs()));
                }
            }
            case MinusNode mn -> {
                // 3-2 -> 1
                if (mn.lhs() instanceof ConstantNode lc && mn.rhs() instanceof ConstantNode rc) {
                    return new ConstantNode(lc.value().subtract(rc.value()));
                } else {
                    return new MinusNode(simplify(mn.lhs()), simplify(mn.rhs()));
                }
            }
            case MultiplyNode mn -> {
                // 2*3 -> 6
                if (mn.lhs() instanceof ConstantNode lc && mn.rhs() instanceof ConstantNode rc) {
                    return new ConstantNode(lc.value().multiply(rc.value()));
                } else {
                    return new MultiplyNode(simplify(mn.lhs()), simplify(mn.rhs()));
                }
            }
            case null -> throw new NullPointerException();
            default -> {
                return root;
            }
        }
    }
}
