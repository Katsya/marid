/*
 * Copyright (C) 2014 Dmitry Ovchinnikov
 * Marid, the free data acquisition and visualization software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.marid.bd.blocks.statements;

import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.marid.bd.IoBlock;
import org.marid.bd.blocks.BdBlock;

import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;

/**
 * @author Dmitry Ovchinnikov
 */
@BdBlock
@XmlRootElement
public class ExpressionBlock extends IoBlock {

    protected Expression expression;

    public ExpressionBlock() {
        super("Expression Statement", "e->s", "e -> s", Color.GREEN.darker(), Expression.class, ExpressionStatement.class);
    }

    @Override
    public void set(Object value) {
        expression = (Expression) value;
    }

    @Override
    public void reset() {
        expression = null;
    }

    @Override
    public ExpressionStatement get() {
        return new ExpressionStatement(expression);
    }
}
