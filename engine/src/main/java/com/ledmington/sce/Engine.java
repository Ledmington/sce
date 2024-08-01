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
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MultiNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.PlusNode;
import com.ledmington.sce.nodes.PowerNode;
import com.ledmington.sce.nodes.VariableNode;

public final class Engine {

    private Engine() {}

    private static boolean containsSameTypeChildren(final MultiNode mn, final Predicate<Node> isSameType) {
        for (int i = 0; i < mn.numChildren(); i++) {
            if (isSameType.test(mn.getChild(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean enoughConstants(final MultiNode mn) {
        final int minimumConstants = 2;
        int count = 0;
        for (int i = 0; i < mn.numChildren(); i++) {
            if (mn.getChild(i).isConstant()) {
                count++;
                if (count >= minimumConstants) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Node simplifyMultiNode(
            final MultiNode mn,
            final Predicate<Node> isSameType,
            final Function<List<Node>, MultiNode> constructor,
            final BinaryOperator<FractionNode> op) {
        if (mn.numChildren() == 1) {
            return mn.getChild(0);
        }

        if (containsSameTypeChildren(mn, isSameType)) {
            // all PlusNode which contain other PlusNodes get flattened
            final List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < mn.numChildren(); i++) {
                if (isSameType.test(mn.getChild(i))) {
                    final MultiNode inner = (MultiNode) mn.getChild(i);
                    for (int j = 0; j < inner.numChildren(); j++) {
                        nodes.add(inner.getChild(j));
                    }
                } else {
                    nodes.add(mn.getChild(i));
                }
            }
            return constructor.apply(nodes);
        }

        if (enoughConstants(mn)) {
            // 1+x+2 = 3+x
            FractionNode r = new FractionNode(mn.identity(), ConstantNode.of(1));
            int first = -1;
            for (int i = 0; i < mn.numChildren(); i++) {
                if (mn.getChild(i) instanceof ConstantNode cn) {
                    r = op.apply(r, new FractionNode(cn, ConstantNode.of(1)));
                    if (first == -1) {
                        first = i;
                    }
                } else if (mn.getChild(i) instanceof FractionNode fn
                        && fn.numerator() instanceof ConstantNode
                        && fn.denominator() instanceof ConstantNode) {
                    r = op.apply(r, fn);
                    if (first == -1) {
                        first = i;
                    }
                }
            }

            final List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < mn.numChildren(); i++) {
                if (i == first) {
                    nodes.add(simplify(r));
                } else if (mn.getChild(i) instanceof ConstantNode
                        || (mn.getChild(i) instanceof FractionNode fn
                                && fn.numerator() instanceof ConstantNode
                                && fn.denominator() instanceof ConstantNode)) {
                    continue;
                } else {
                    nodes.add(simplify(mn.getChild(i)));
                }
            }
            return constructor.apply(nodes);
        }

        final List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < mn.numChildren(); i++) {
            nodes.add(simplify(mn.getChild(i)));
        }
        return constructor.apply(nodes);
    }

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
            case PlusNode pn -> simplifyMultiNode(pn, x -> x instanceof PlusNode, l -> new PlusNode(l), (a, b) -> {
                final BigInteger d1 = ((ConstantNode) a.denominator()).value();
                final BigInteger d2 = ((ConstantNode) b.denominator()).value();
                final BigInteger d = d1.multiply(d2);
                final BigInteger n1 = ((ConstantNode) a.numerator()).value();
                final BigInteger n2 = ((ConstantNode) b.numerator()).value();
                final BigInteger n = n1.multiply(d2).add(n2.multiply(d1));
                return new FractionNode(new ConstantNode(n), new ConstantNode(d));
            });
            case MultiplyNode mn -> simplifyMultiNode(
                    mn, x -> x instanceof MultiplyNode, l -> new MultiplyNode(l), (a, b) -> {
                        final BigInteger d1 = ((ConstantNode) a.denominator()).value();
                        final BigInteger d2 = ((ConstantNode) b.denominator()).value();
                        final BigInteger d = d1.multiply(d2);
                        final BigInteger n1 = ((ConstantNode) a.numerator()).value();
                        final BigInteger n2 = ((ConstantNode) b.numerator()).value();
                        final BigInteger n = n1.multiply(n2);
                        return new FractionNode(new ConstantNode(n), new ConstantNode(d));
                    });
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
            case PowerNode pn -> {
                if (pn.base() instanceof ConstantNode bn && pn.exponent() instanceof ConstantNode en) {
                    yield new ConstantNode(bn.value().pow(en.value().intValue()));
                }
                if (pn.base() instanceof FractionNode fn
                        && fn.numerator() instanceof ConstantNode num
                        && fn.denominator() instanceof ConstantNode den) {
                    yield new FractionNode(
                            simplify(new PowerNode(num, pn.exponent())), simplify(new PowerNode(den, pn.exponent())));
                }
                yield new PowerNode(simplify(pn.base()), simplify(pn.exponent()));
            }
            case null -> throw new NullPointerException();
            default -> root;
        };
    }
}
