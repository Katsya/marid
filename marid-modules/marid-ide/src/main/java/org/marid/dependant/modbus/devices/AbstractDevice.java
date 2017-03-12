/*
 * Copyright (c) 2016 Dmitry Ovchinnikov
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

package org.marid.dependant.modbus.devices;

import com.digitalpetri.modbus.FunctionCode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.marid.dependant.modbus.ModbusPane;
import org.marid.dependant.modbus.annotation.Modbus;
import org.marid.dependant.modbus.codec.CodecManager;
import org.marid.dependant.modbus.codec.ModbusCodec;
import org.marid.dependant.modbus.devices.info.AbstractDeviceInfo;
import org.marid.jfx.converter.MaridConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.PostConstruct;

import static javafx.collections.FXCollections.observableArrayList;
import static org.marid.jfx.LocalizedStrings.ls;
import static org.marid.jfx.icons.FontIcon.D_CLOSE_BOX;
import static org.marid.jfx.icons.FontIcon.D_TOOLTIP_EDIT;
import static org.marid.jfx.icons.FontIcons.glyphIcon;

/**
 * @author Dmitry Ovchinnikov.
 * @since 0.9
 */
public abstract class AbstractDevice<I extends AbstractDeviceInfo, T> extends BorderPane {

    final Class<I> deviceInfoType;
    final Class<T> type;
    final HBox titleBox;
    final TextField title;
    final Button editButton;
    final Button closeButton;
    final HBox addressBox;
    final Label address;
    final ComboBox<FunctionCode> functions;
    final ComboBox<ModbusCodec<T>> codec;

    AbstractDevice(Class<I> deviceInfoType, Class<T> type) {
        this.deviceInfoType = deviceInfoType;
        this.type = type;
        setBackground(new Background(new BackgroundFill(new Color(0.5, 0.5, 0.5, 0.2), null, null)));
        setTop(titleBox = new HBox(4,
                title = new TextField(),
                editButton = new Button(),
                closeButton = new Button()));
        setBottom(addressBox = new HBox(4,
                address = new Label("0000"),
                functions = new ComboBox<>(observableArrayList(FunctionCode.values())),
                codec = new ComboBox<>())
        );
        setPadding(new Insets(5));
        titleBox.setAlignment(Pos.BASELINE_LEFT);
        titleBox.setPadding(new Insets(4));
        addressBox.setAlignment(Pos.BASELINE_LEFT);
        addressBox.setPadding(new Insets(4));
        title.textProperty().bind(ls(getClass().getSimpleName()));
        editButton.setGraphic(glyphIcon(D_TOOLTIP_EDIT, 16));
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox.setHgrow(codec, Priority.ALWAYS);
        functions.getSelectionModel().select(FunctionCode.ReadHoldingRegisters);
        functions.setConverter(new MaridConverter<>(f -> String.format("%02X", f.getCode())));
        codec.setMaxWidth(Double.MAX_VALUE);
        codec.setConverter(new MaridConverter<>(ModbusCodec::getName));
    }

    @PostConstruct
    private void initCloseButton() {
        closeButton.setGraphic(glyphIcon(D_CLOSE_BOX, 16));
        closeButton.setOnAction(event -> ((ModbusPane) getParent()).getChildren().remove(this));
    }

    @Autowired
    private void initEditButton(@Modbus Stage stage, GenericApplicationContext ctx) {
        editButton.setOnAction(event -> ctx.getBean(getEditor(), this, stage).showAndWait().ifPresent(this::setInfo));
    }

    @Autowired
    private void initCodec(CodecManager codecManager) {
        codec.setItems(codecManager.getCodecs(type));
        codec.getSelectionModel().select(0);
    }

    private I newInfo() {
        try {
            return deviceInfoType.newInstance();
        } catch (Exception x) {
            throw new IllegalStateException(x);
        }
    }

    public I getInfo() {
        final I info = newInfo();
        info.address = getAddress();
        info.codec = codec.getValue().getName();
        info.function = functions.getValue();
        return info;
    }

    public void setInfo(I info) {
        address.setText(String.format("%04X", info.address));
        codec.getItems().filtered(e -> e.getName().equals(info.codec)).forEach(codec.getSelectionModel()::select);
        functions.getSelectionModel().select(info.function);
    }

    public int getAddress() {
        return Integer.parseInt(address.getText(), 16);
    }

    public FunctionCode getFunctionCode() {
        return functions.getValue();
    }

    public abstract byte[] getData();

    public abstract Class<? extends AbstractDeviceEditor<I, T, ? extends AbstractDevice<I, T>>> getEditor();
}
