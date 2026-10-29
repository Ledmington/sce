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

/** A Node which can have multiple children nodes with commutativity. */
public interface MultiNode extends Node {

	/**
	 * Returns the identity element of the operation represented by this node.
	 *
	 * @return The identity element.
	 */
	ConstantNode identity();

	/**
	 * Returns the number of children of this node, counting duplicates.
	 *
	 * @return The number of children.
	 */
	int getNumChildren();

	/**
	 * Returns the child at the given index.
	 *
	 * @param idx The index of the child.
	 * @return The child at the given index.
	 */
	Node getChild(final int idx);
}
