/*
 * sce - Symbolic Calculus Engine
 * Copyright (C) 2024-2026 Filippo Barbari <filippo.barbari@gmail.com>
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

/** A node of the abstract syntax tree of an expression. */
public interface Node {

	/**
	 * Checks whether this node contains only constant values.
	 *
	 * @return True if this node is constant, false otherwise.
	 */
	boolean isConstant();

	/**
	 * Returns the number of nodes in the subtree rooted at this node.
	 *
	 * @return The size of this subtree.
	 */
	int size();

	/**
	 * Returns the plain-text representation of this node.
	 *
	 * @return The expression represented by this node.
	 */
	String toExpression();

	/**
	 * Returns the LaTeX representation of this node.
	 *
	 * @return The LaTeX code of this node.
	 */
	String toLatex();
}
