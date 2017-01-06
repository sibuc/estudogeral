package org.dspace.app.stats;

import java.sql.SQLException;
import java.util.Date;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class WorkflowAgreggateManager {

	public static void agreggate() {
		Context context;
		try {
			context = new Context();

                        deleteDuplicates(context);
                        context.commit();

			DatabaseManager.updateQuery(context, "DELETE FROM stats.workflow_intervals");
			String query = "SELECT item_id, date, time FROM stats.workflow where old_state=6";
			
			TableRowIterator iterator = DatabaseManager.query(context, query); //, params.toArray());
			TableRow row = null;
			while (iterator.hasNext()) {
			        row = iterator.next();
				
				Date timeEnd = row.getDateColumn("time");
				Date dateEnd = row.getDateColumn("date");

                                String sql = "select date, time from stats.workflow where item_id=? and old_state!=6 order by date desc, time desc limit 1";
                                TableRowIterator iteratorFirst = DatabaseManager.query(context, sql, row.getIntColumn("item_id"));
                                if (iteratorFirst.hasNext())
                                {
                                   TableRow rowFirst = iteratorFirst.next();
                                   Date timeStart = rowFirst.getDateColumn("time");
                                   Date dateStart = rowFirst.getDateColumn("date");
                                   if (dateStart != null)
                                   {
                                      long diff = (timeEnd.getTime() - timeStart.getTime()) + (dateEnd.getTime() - dateStart.getTime());
                                      try {
                                         DatabaseManager.updateQuery(context, "INSERT INTO stats.workflow_intervals VALUES (?,?)", row.getIntColumn("item_id"), diff);
                                      } catch (SQLException e) {
                                         System.out.println("INFO: Item "+row.getIntColumn("item_id")+" ignored, already computed.");
                                      }
                                   }
                                 }
                                context.commit();
			}
			
			context.complete();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

   private static void deleteDuplicates(Context context) throws SQLException
   {
      String sql = "SELECT item_id, old_state, count(*) AS numero FROM stats.workflow WHERE old_state=6 GROUP BY item_id, old_state HAVING count(*) > 1";
      TableRowIterator iterator = DatabaseManager.query(context, sql);
      TableRow row = null;
      while(iterator.hasNext())
      {
         row = iterator.next();

         String query = "delete from stats.workflow where item_id=? and old_state=6 and workflow_id not in (select max(workflow_id) from stats.workflow where item_id=? and old_state=6);";

         Object[] params = new Object[2];
         params[0] = row.getIntColumn("item_id");
         params[1] = row.getIntColumn("item_id");
         DatabaseManager.updateQuery(context, query, params);
      }
      context.commit();
   }

}
