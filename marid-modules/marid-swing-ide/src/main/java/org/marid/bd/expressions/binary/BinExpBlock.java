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

package org.marid.bd.expressions.binary;

import images.Images;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.marid.bd.Block;
import org.marid.bd.BlockComponent;

import javax.swing.*;
import java.awt.*;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Dmitry Ovchinnikov
 */
public class BinExpBlock extends Block {

    protected Expression left;
    protected Expression right;
    protected TokenType tokenType;
    protected final In<Expression> leftInput = new In<>("L", Expression.class, e -> left = e, () -> left = null);
    protected final In<Expression> rightInput = new In<>("R", Expression.class, e -> right = e, () -> right = null);
    protected final Out<Expression> output = new Out<>(">", Expression.class, this::binaryExpression);

    public BinExpBlock() {
        this(TokenType.PLUS);
    }

    @ConstructorProperties({"tokenType"})
    public BinExpBlock(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public BlockComponent createComponent() {
        return new BinExpComponent(this);
    }

    @Override
    public BinExpEditor createWindow(Window parent) {
        return new BinExpEditor(parent, this);
    }

    @Override
    public List<Input<?>> getInputs() {
        return Arrays.asList(leftInput, rightInput);
    }

    @Override
    public List<Output<?>> getOutputs() {
        return Collections.singletonList(output);
    }

    @Override
    public ImageIcon getVisualRepresentation() {
        return Images.getIconFromText(" 2 ", 32, 32, Color.BLUE, Color.WHITE);
    }

    @Override
    public String getName() {
        return "Binary Expression";
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType token) {
        fire(BinExpListener.class, () -> tokenType, t -> tokenType = t, token, BinExpListener::changedTokenType);
    }

    public BinaryExpression binaryExpression() {
        return new BinaryExpression(left, tokenType.token, right);
    }

    public static enum TokenType {

        PLUS(Token.newPlaceholder(Types.PLUS), " + "),
        MINUS(Token.newPlaceholder(Types.MINUS), " - "),
        PRODUCT(Token.newPlaceholder(Types.STAR), " * "),
        DIVISION(Token.newPlaceholder(Types.DIVIDE), " / "),
        POWER(Token.newPlaceholder(Types.STAR_STAR), " ** "),
        INSTANCEOF(Token.newPlaceholder(Types.KEYWORD_INSTANCEOF), " i.of"),
        IN(Token.newPlaceholder(Types.KEYWORD_IN), "in"),
        AS(Token.newPlaceholder(Types.KEYWORD_AS), "as");

        public final Token token;
        public final ImageIcon icon;

        private TokenType(Token token, String text) {
            this.token = token;
            this.icon = Images.getIconFromText(text, 32, 32, Color.BLUE, Color.WHITE);
        }

        public ImageIcon getIcon(int size) {
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
    }
}
