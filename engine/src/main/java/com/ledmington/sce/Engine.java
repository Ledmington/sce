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

import com.ledmington.sce.nodes.BracketNode;
import com.ledmington.sce.nodes.ConstantNode;
import com.ledmington.sce.nodes.FractionNode;
import com.ledmington.sce.nodes.MinusNode;
import com.ledmington.sce.nodes.MultiplyNode;
import com.ledmington.sce.nodes.Node;
import com.ledmington.sce.nodes.PlusNode;

public final class Engine {

    private Engine() {}

    public static Node simplify(final Node root) {
        return switch (root) {
            case BracketNode bn -> {
                // (2) -> 2
                if (bn.inner() instanceof ConstantNode cn) {
                    yield cn;
                } else if (bn.inner() instanceof FractionNode fn) {
                    yield new FractionNode(simplify(fn.numerator()), simplify(fn.denominator()));
                } else {
                    yield new BracketNode(simplify(bn.inner()));
                }
            }
            case PlusNode pn -> {
				switch (pn.lhs()) {
					case ConstantNode lc when pn.rhs() instanceof ConstantNode rc -> {
						yield new ConstantNode(lc.value().add(rc.value()));
					}
					case ConstantNode left when pn.rhs() instanceof FractionNode right -> {
						yield new FractionNode(
								simplify(new PlusNode(new MultiplyNode(left, right.denominator()), right.numerator())),
								simplify(right.denominator()));
					}
                    case FractionNode left when pn.rhs() instanceof ConstantNode right -> {
                        yield new FractionNode(
                                simplify(new PlusNode(left.numerator(), new MultiplyNode(right,left.denominator()))),
                                simplify(left.denominator())
                        );
                    }
					case FractionNode left when pn.rhs() instanceof FractionNode right -> {
						yield new FractionNode(
								simplify(new PlusNode(
										new MultiplyNode(left.numerator(), right.denominator()),
										new MultiplyNode(right.numerator(), left.denominator()))),
								simplify(new MultiplyNode(left.denominator(), right.denominator())));
					}
					case null, default -> {
						yield new PlusNode(simplify(pn.lhs()), simplify(pn.rhs()));
					}
				}
            }
            case MinusNode mn -> {
                // 3-2 -> 1
                if (mn.lhs() instanceof ConstantNode lc && mn.rhs() instanceof ConstantNode rc) {
                    yield new ConstantNode(lc.value().subtract(rc.value()));
                } else if (mn.lhs() instanceof FractionNode left && mn.rhs() instanceof FractionNode right) {
                    yield new FractionNode(
                            simplify(new MinusNode(
                                    new MultiplyNode(left.numerator(), right.denominator()),
                                    new MultiplyNode(right.numerator(), left.denominator()))),
                            simplify(new MultiplyNode(left.denominator(), right.denominator())));
                } else {
                    yield new MinusNode(simplify(mn.lhs()), simplify(mn.rhs()));
                }
            }
            case MultiplyNode mn -> {
                // 2*3 -> 6
                if (mn.lhs() instanceof ConstantNode lc && mn.rhs() instanceof ConstantNode rc) {
                    yield new ConstantNode(lc.value().multiply(rc.value()));
                } else if (mn.lhs() instanceof FractionNode left && mn.rhs() instanceof FractionNode right) {
                    yield new FractionNode(
                            simplify(new MultiplyNode(left.numerator(), right.numerator())),
                            simplify(new MultiplyNode(left.denominator(), right.denominator())));
                } else {
                    yield new MultiplyNode(simplify(mn.lhs()), simplify(mn.rhs()));
                }
            }
            case FractionNode fn -> {
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
