package sqlrunner.dbext.extensions;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import sqlrunner.datamodel.SQLTable;
import sqlrunner.dbext.GenericDatabaseExtension;
import dbtools.DatabaseSession;

public class H2Extension extends GenericDatabaseExtension {

	private static final String name = "Derby Extension";
	private static final Logger logger = Logger.getLogger(H2Extension.class);
	
	public H2Extension() {
		addDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
		addDriverClassName("org.apache.derby.jdbc.ClientDriver");
	}

	@Override
	public boolean hasExplainFeature() {
		return true;
	}

	@Override
	public String getExplainSQL(String currentStatement) {
		if (currentStatement != null) {
			currentStatement = currentStatement.trim();
			StringBuilder sb = new StringBuilder();
			sb.append("call syscs_util.syscs_set_runtimestatistics(1);\n");
			sb.append(currentStatement);
			if (currentStatement.endsWith(";") == false) {
				sb.append(";\n");
			}
			sb.append("VALUES SYSCS_UTIL.SYSCS_GET_RUNTIMESTATISTICS();\n");
			sb.append("call syscs_util.syscs_set_runtimestatistics(0);");
			return sb.toString();
		} else {
			return "";
		}
	}

	@Override
	public String setupViewSQLCode(DatabaseSession session, SQLTable table) {
		if (table.isView()) {
			if (logger.isDebugEnabled()) {
				logger.debug("setupViewSQLCode view=" + table.getAbsoluteName());
			}
			StringBuilder sb = new StringBuilder();
			sb.append("select v.VIEWDEFINITION ");
			sb.append(" from SYS.SYSVIEWS v, SYS.SYSTABLES t, SYS.SYSSCHEMAS s");
			sb.append(" where t.TABLEID = v.TABLEID");
			sb.append(" and t.SCHEMAID = s.SCHEMAID");
			sb.append(" and t.TABLENAME = '");
			sb.append(table.getName());
			sb.append("' and s.SCHEMANAME = '");
			sb.append(table.getSchema().getName());
			sb.append("'");
			String source = null;
			try {
				ResultSet rs = session.executeQuery(sb.toString());
				if (session.isSuccessful()) {
					if (rs.next()) {
						source = rs.getString(1);
						if (source != null && source.isEmpty() == false) {
							table.setSourceCode(source);
						}
					}
				}
			} catch (SQLException sqle) {
				logger.error("setupViewSQLCode for table " + table.getAbsoluteName() + " failed: " + sqle.getMessage(), sqle);
			}
			return source;
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIdentifierQuoteString() {
		return "\"";
	}

}