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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.nodes.VariableNode;

public final class Engine {

    private Engine() {}

    public static Node simplify(final Node root) {
        return switch (root) {
            case BracketNode bn -> {
                switch (bn.inner()) {
                    case ConstantNode cn -> {
                        yield cn;
                    }
                    case FractionNode fn -> {
                        yield new FractionNode(simplify(fn.numerator()), simplify(fn.denominator()));
                    }
                    case VariableNode vn -> {
                        yield vn;
                    }
                    default -> {
                        yield new BracketNode(simplify(bn.inner()));
                    }
                }
            }
            case PlusNode pn -> {
                if (pn.size() == 1) {
                    yield pn.get(0);
                } else if (pn.stream().anyMatch(n -> n instanceof PlusNode)) {
                    // 1+(2+3) = 1+2+3
                    final List<Node> nodes = new ArrayList<>();
                    for (int i = 0; i < pn.size(); i++) {
                        if (pn.get(i) instanceof PlusNode inner) {
                            for (int j = 0; j < inner.size(); j++) {
                                nodes.add(inner.get(j));
                            }
                        } else {
                            nodes.add(pn.get(i));
                        }
                    }
                    yield new PlusNode(nodes);
                } else if (pn.stream().filter(Node::isConstant).count() >= 2) {
                    // 1+x+2 = 3+x
                    FractionNode r = FractionNode.of(0, 1);
                    int first = -1;
                    for (int i = 0; i < pn.size(); i++) {
                        if (pn.get(i) instanceof ConstantNode cn) {
                            r = new FractionNode(
                                    new PlusNode(r.numerator(), new MultiplyNode(cn, r.denominator())),
                                    r.denominator());
                            if (first == -1) {
                                first = i;
                            }
                        } else if (pn.get(i) instanceof FractionNode fn
                                && fn.numerator() instanceof ConstantNode num
                                && fn.denominator() instanceof ConstantNode den) {
                            r = new FractionNode(
                                    new PlusNode(
                                            new MultiplyNode(fn.numerator(), den),
                                            new MultiplyNode(fn.denominator(), num)),
                                    new MultiplyNode(fn.denominator(), den));
                            if (first == -1) {
                                first = i;
                            }
                        }
                    }
                    final List<Node> nodes = new ArrayList<>();
                    for (int i = 0; i < pn.size(); i++) {
                        if (i == first) {
                            nodes.add(simplify(r));
                        } else if (pn.get(i) instanceof ConstantNode || pn.get(i) instanceof FractionNode) {
                            throw new Error("Not implemented");
                        } else {
                            nodes.add(simplify(pn.get(i)));
                        }
                    }
                    yield new PlusNode(nodes);
                } else {
                    yield new PlusNode(pn.stream().map(Engine::simplify).toList());
                }
            }
            case MultiplyNode mn -> {
                if (mn.size() == 1) {
                    yield mn.stream().findAny().orElseThrow();
                } else if (mn.stream().anyMatch(n -> n instanceof MultiplyNode)) {
                    // 1*(2*3) = 1*2*3
                    final List<Node> nodes = new ArrayList<>();
                    for (int i = 0; i < mn.size(); i++) {
                        if (mn.get(i) instanceof MultiplyNode inner) {
                            for (int j = 0; j < inner.size(); j++) {
                                nodes.add(inner.get(j));
                            }
                        } else {
                            nodes.add(mn.get(i));
                        }
                    }
                    yield new MultiplyNode(nodes);
                } else if (mn.stream().filter(Node::isConstant).count() >= 2) {
                    // 1+x+2 = 3+x
                    FractionNode r = FractionNode.of(1, 1);
                    int first = -1;
                    for (int i = 0; i < mn.size(); i++) {
                        if (mn.get(i) instanceof ConstantNode cn) {
                            r = new FractionNode(new MultiplyNode(r.numerator(), cn), r.denominator());
                            if (first == -1) {
                                first = i;
                            }
                        } else if (mn.get(i) instanceof FractionNode fn
                                && fn.numerator() instanceof ConstantNode num
                                && fn.denominator() instanceof ConstantNode den) {
                            r = new FractionNode(
                                    new MultiplyNode(fn.numerator(), num), new MultiplyNode(fn.denominator(), den));
                            if (first == -1) {
                                first = i;
                            }
                        }
                    }
                    final List<Node> nodes = new ArrayList<>();
                    for (int i = 0; i < mn.size(); i++) {
                        if (i == first) {
                            nodes.add(simplify(r));
                        } else if (mn.get(i) instanceof ConstantNode || mn.get(i) instanceof FractionNode) {
                            throw new Error("Not implemented");
                        } else {
                            nodes.add(simplify(mn.get(i)));
                        }
                    }
                    yield new MultiplyNode(nodes);
                } else {
                    yield new MultiplyNode(mn.stream().map(Engine::simplify).toList());
                }
            }
            case FractionNode fn -> {
                if (fn.denominator() instanceof ConstantNode cn && cn.value().compareTo(BigInteger.ONE) == 0) {
                    yield fn.numerator();
                }
                if (fn.numerator() instanceof FractionNode num && fn.denominator() instanceof ConstantNode cn) {
                    yield new FractionNode(
                            simplify(num.numerator()), simplify(new MultiplyNode(num.denominator(), cn)));
                }
                if (fn.numerator() instanceof ConstantNode cn && fn.denominator() instanceof FractionNode den) {
                    yield new FractionNode(
                            simplify(new MultiplyNode(cn, den.denominator())), simplify(den.numerator()));
                }
                if (fn.numerator() instanceof FractionNode num && fn.denominator() instanceof FractionNode den) {
                    yield new FractionNode(
                            simplify(new MultiplyNode(num.numerator(), den.denominator())),
                            simplify(new MultiplyNode(num.denominator(), den.numerator())));
                }
                if (fn.numerator() instanceof ConstantNode num && fn.denominator() instanceof ConstantNode den) {
                    final BigInteger mcd = num.value().gcd(den.value());
                    final boolean isNumeratorNegative = num.value().compareTo(BigInteger.ZERO) < 0;
                    final boolean isDenominatorNegative = den.value().compareTo(BigInteger.ZERO) < 0;
                    final BigInteger newNumerator = num.value().divide(mcd).abs();
                    final BigInteger newDenominator = den.value().divide(mcd).abs();

                    if (isNumeratorNegative && isDenominatorNegative) {
                        yield new FractionNode(new ConstantNode(newNumerator), new ConstantNode(newDenominator));
                    }

                    yield new FractionNode(
                            new ConstantNode(
                                    (isNumeratorNegative || isDenominatorNegative)
                                            ? newNumerator.multiply(new BigInteger("-1"))
                                            : newNumerator),
                            new ConstantNode(newDenominator));
                }

                yield new FractionNode(simplify(fn.numerator()), simplify(fn.denominator()));
            }
            case null -> throw new NullPointerException();
            default -> root;
        };
    }
}
