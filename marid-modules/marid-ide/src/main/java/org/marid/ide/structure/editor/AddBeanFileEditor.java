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

package org.marid.ide.structure.editor;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.printer.PrettyPrinter;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import org.marid.ide.project.ProjectManager;
import org.marid.ide.project.ProjectProfile;
import org.marid.jfx.icons.FontIcons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.WARNING;
import static org.marid.ide.IdeNotifications.n;
import static org.marid.l10n.L10n.s;

/**
 * @author Dmitry Ovchinnikov
 */
@Component
public class AddBeanFileEditor extends AbstractFileEditor<ProjectProfile> {

    private final ProjectManager projectManager;
    private final PrettyPrinter prettyPrinter;

    @Autowired
    public AddBeanFileEditor(ProjectManager projectManager, PrettyPrinter prettyPrinter) {
        super(Files::isDirectory);
        this.projectManager = projectManager;
        this.prettyPrinter = prettyPrinter;
    }

    @Nonnull
    @Override
    public String getName() {
        return "Add a bean file";
    }

    @Nonnull
    @Override
    public Node getIcon() {
        return FontIcons.glyphIcon("M_SETTINGS_INPUT_COMPONENT", 16);
    }

    @Nonnull
    @Override
    public String getGroup() {
        return "bean";
    }

    @Override
    protected ProjectProfile editorContext(@Nonnull Path path) {
        return projectManager.getProfile(path)
                .filter(p -> path.startsWith(p.getSrcMainJava()) || path.startsWith(p.getSrcTestJava()))
                .orElse(null);
    }

    @Override
    protected void edit(@Nonnull Path file, @Nonnull ProjectProfile profile) {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(s("Add a bean file"));
        dialog.setContentText(s("Bean file name") + ": ");
        final String javaFileName = dialog.showAndWait().orElse(null);

        if (javaFileName == null) {
            return;
        }

        final Path basePath = file.startsWith(profile.getSrcMainJava())
                ? profile.getSrcMainJava()
                : profile.getSrcTestJava();
        final Path relativePath = basePath.relativize(file);
        final String packageName = StreamSupport.stream(relativePath.spliterator(), false)
                .map(Path::toString)
                .collect(Collectors.joining("."));

        final CompilationUnit compilationUnit = new CompilationUnit(packageName);
        compilationUnit.addImport(Generated.class);

        final ClassOrInterfaceDeclaration klass = compilationUnit.addClass(javaFileName, Modifier.PUBLIC);
        klass.addAnnotation(new SingleMemberAnnotationExpr(new Name("Generated"), new StringLiteralExpr("Marid")));

        final String source = prettyPrinter.print(compilationUnit);

        final Path path = file.resolve(javaFileName + ".java");
        try {
            Files.write(path, source.getBytes(UTF_8));
        } catch (Exception x) {
            n(WARNING, "Unable to save file {0}", x, file);
        }
    }
}