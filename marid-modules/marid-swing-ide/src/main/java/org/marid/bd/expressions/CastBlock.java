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

package org.marid.bd.expressions;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.marid.bd.StandardBlock;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Ovchinnikov
 */
public class CastBlock extends StandardBlock {

    protected Expression expression;
    protected ClassNode classNode;

    protected final In<Expression> exprInput = new In<>("expr", Expression.class, e -> expression = e);
    protected final In<ClassNode> classInput = new In<>("class", ClassNode.class, c -> classNode = c);
    protected final Out<CastExpression> castExpr = new Out<>("out", CastExpression.class, () -> new CastExpression(classNode, expression));

    public CastBlock() {
        super("Cast Expression", "(*)", "(*)", Color.BLUE);
    }

    @Override
    public void reset() {
        expression = EmptyExpression.INSTANCE;
        classNode = ClassHelper.OBJECT_TYPE;
    }

    @Override
    public List<Input<?>> getInputs() {
        return Arrays.asList(exprInput, classInput);
    }

    @Override
    public List<Output<?>> getOutputs() {
        return Collections.singletonList(castExpr);
    }
}