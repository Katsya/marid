/*
 * Copyright (c) 2017 Dmitry Ovchinnikov
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

package org.marid.ide.panes.main;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.marid.ide.project.ProjectManager;
import org.marid.ide.project.ProjectProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.marid.jfx.icons.FontIcons.glyphIcon;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class IdeStatusBar extends BorderPane {

    private final HBox right;
    private final ToolBar toolBar;

    public IdeStatusBar() {
        setFocusTraversable(false);
        setCenter(toolBar = new ToolBar());
        setRight(right = new HBox(10));
        BorderPane.setMargin(toolBar, new Insets(5, 5, 5, 5));
        BorderPane.setMargin(right, new Insets(5, 5, 5, 5));
    }

    @Order(1)
    @Autowired
    public void initProfile(ProjectManager manager) {
        final ComboBox<ProjectProfile> combo = new ComboBox<>(manager.getProfiles());
        final SelectionModel<ProjectProfile> selection = combo.getSelectionModel();
        selection.select(manager.getProfile());
        final ObjectProperty<ProjectProfile> profile = manager.profileProperty();
        profile.addListener((observable, oldValue, newValue) -> selection.select(newValue));
        selection.selectedItemProperty().addListener((observable, oldValue, newValue) -> profile.set(newValue));
        right.getChildren().add(combo);
    }

    @Order(2)
    @Autowired
    public void initDateTime(ScheduledExecutorService timer) throws Exception {
        final Label timeLabel = new Label("", glyphIcon("O_CLOCK", 16));
        final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4)
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral(' ')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .toFormatter();
        timeLabel.setMaxHeight(Double.MAX_VALUE);
        timer.scheduleWithFixedDelay(() -> {
            final ZonedDateTime now = Instant.now().atZone(ZoneId.systemDefault());
            final String time = now.format(timeFormatter);
            Platform.runLater(() -> timeLabel.setText(time));
        }, 1_000L, 1_000L, TimeUnit.MILLISECONDS);
        right.getChildren().add(timeLabel);
    }

    public void add(Button button) {
        toolBar.getItems().add(button);
    }

    public void remove(Button button) {
        toolBar.getItems().remove(button);
    }
}
