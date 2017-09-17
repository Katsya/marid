/*-
 * #%L
 * marid-ide
 * %%
 * Copyright (C) 2012 - 2017 MARID software development group
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.marid.dependant.beaneditor;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.converter.DefaultStringConverter;
import org.marid.dependant.beaneditor.model.SignatureResolver;
import org.marid.ide.model.BeanMethodArgData;
import org.marid.ide.project.ProjectProfile;
import org.marid.jfx.annotation.DisableStdSelectAndRemoveActions;
import org.marid.jfx.annotation.DisableStdUpAndDownActions;
import org.marid.jfx.control.MaridTableView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.misc.Builder.build;

/**
 * @author Dmitry Ovchinnikov
 */
@DisableStdUpAndDownActions
@DisableStdSelectAndRemoveActions
public abstract class BeanMethodArgTable extends MaridTableView<BeanMethodArgData> {

    protected final TableColumn<BeanMethodArgData, HBox> nameColumn;
    protected final TableColumn<BeanMethodArgData, String> typeColumn;
    protected final TableColumn<BeanMethodArgData, String> valueColumn;

    public BeanMethodArgTable() {
        setEditable(true);

        nameColumn = build(new TableColumn<>(), column -> {
            column.textProperty().bind(ls("Name"));
            column.setMinWidth(70);
            column.setPrefWidth(80);
            column.setPrefWidth(350);
            getColumns().add(column);
        });

        typeColumn = build(new TableColumn<>(), column -> {
            column.textProperty().bind(ls("Type"));
            column.setMinWidth(70);
            column.setPrefWidth(80);
            column.setPrefWidth(350);
            getColumns().add(column);
        });

        valueColumn = build(new TableColumn<>(), column -> {
            column.textProperty().bind(ls("Value"));
            column.setMinWidth(200);
            column.setPrefWidth(400);
            column.setPrefWidth(1000);
            column.setCellValueFactory(param -> param.getValue().value);
            column.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));
            column.setEditable(true);
            getColumns().add(column);
        });

        setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, null, null)));
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
    }

    @Autowired
    public void initTypeColumn(SignatureResolver signatureResolver,
                               ProjectProfile profile,
                               BeanEditorContext context) {
        typeColumn.setCellValueFactory(param -> Bindings.createStringBinding(() -> {
            final BeanMethodArgData arg = param.getValue();
            return signatureResolver.postProcess(context.formalType(arg).getTypeName());
        }, profile.getBeanFile().children));
    }

    @PostConstruct
    public void initNameColumn() {
        nameColumn.setCellValueFactory(param -> Bindings.createObjectBinding(() -> {
            final String name = param.getValue().getName();
            final String type = param.getValue().getType();
            final HBox box = new HBox(3);
            box.getChildren().add(new Label(name));
            if (!"of".equals(type)) {
                box.getChildren().add(new Separator(Orientation.VERTICAL));
                final Label label = new Label(type);
                label.setUnderline(true);
                box.getChildren().add(label);
            }
            return box;
        }, param.getValue().name, param.getValue().type));
    }

    @Autowired
    public void initRowFactory(List<BeanMethodArgAction> actionProviders) {
        actions().addAll(actionProviders);
    }
}
