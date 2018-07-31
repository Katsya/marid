/*-
 * #%L
 * marid-db
 * %%
 * Copyright (C) 2012 - 2018 MARID software development group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.hsqldb.jdbc;

import org.hsqldb.Database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Dmitry Ovchinnikov.
 */
public class JDBCSessionDataSource extends JDBCDataSource {

  private final Database database;
  private final String schema;

  public JDBCSessionDataSource(Database database, String schema) {
    this.database = database;
    this.schema = schema;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return new JDBCSessionConnection(database, schema);
  }
}
