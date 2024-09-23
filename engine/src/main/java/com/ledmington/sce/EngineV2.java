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

import java.util.Set;

import com.ledmington.sce.engine.Move;
import com.ledmington.sce.nodes.Node;

public final class EngineV2 {

    private EngineV2() {}

    private static Set<Move> getAvailableMoves(final Node root) {
        return Set.of();
    }

    public static Node simplify(final Node root) {
        final Set<Move> moves = getAvailableMoves(root);
        Move bestMove = null;
        int bestScore = root.size();
        for (final Move m : moves) {
            if (m.score() < bestScore) {
                bestScore = m.score();
                bestMove = m;
            }
        }

        if (bestMove == null) {
            // no better move found
            return root;
        }

        return bestMove.apply(root);
    }
}
