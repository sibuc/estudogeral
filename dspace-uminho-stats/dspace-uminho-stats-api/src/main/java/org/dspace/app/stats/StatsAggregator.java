/*
 * StatsAggregator.java
 *
 * Version: $Revision:  $
 *
 * Date: $Date:  $
 *
 * Copyright (C) 2008, the DSpace Foundation.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     - Neither the name of the DSpace Foundation nor the names of their
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package org.dspace.app.stats;

import java.util.Calendar;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.dspace.core.Context;
import org.dspace.storage.rdbms.DatabaseManager;
import org.dspace.storage.rdbms.TableRow;
import org.dspace.storage.rdbms.TableRowIterator;

public class StatsAggregator
{

    private static boolean isVerbose = false;
    private static boolean aggregate = false;
    private static boolean clean = false;
    private static boolean recalculate = false;
    private static boolean workflow = false;
    private static String counters = null;
    private static List tables = new ArrayList();
    private static String tablesLine = null;
    private static String yearMonth = "all";
    private static String spiders = "notclosed"; 
    
    private static ArrayList<EventTable> eventTables = new ArrayList<EventTable>();
    
    public static void main(String[] argv) throws Exception
    {        
        readCommandLineOptions(argv);
        
        initAggregations();

        //if (recalculate) new DownloadViewCounterAgreggateManager(counters, true);
        //else new DownloadViewCounterAgreggateManager(counters, false);
        
        if (workflow) WorkflowAgreggateManager.agreggate();
        
        processAggregations();   
    }
    
    private static void processAggregations()
    { 

        Context context = null;
        
        try
        {
            context = new Context();
            boolean test = aggregationOngoing(context);

            if (test)
            {
               System.out.println("You have an ongoing aggregation");
               System.exit(0);
            }

            Date startDate;
            Date endDate;
            if (yearMonth == "all")
            {
               java.util.Date now = new java.util.Date();
               startDate = closedDate(context);
               
               Calendar cal = Calendar.getInstance();
               cal.setTime(startDate);
               cal.add(Calendar.DATE, 1);
               java.util.Date temp = cal.getTime();
        
               startDate = new Date(temp.getTime());
               endDate = new Date(now.getTime());
            }
            else
            {
               Calendar cal = Calendar.getInstance();
               cal.set(Integer.parseInt(yearMonth.substring(0,4)), Integer.parseInt(yearMonth.substring(4))-1, 1);

               Calendar calEnd = Calendar.getInstance();
               calEnd.set(Integer.parseInt(yearMonth.substring(0,4)), Integer.parseInt(yearMonth.substring(4))-1, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

               java.util.Date start = cal.getTime();
               java.util.Date end = calEnd.getTime();

               startDate = new Date(start.getTime());
               endDate = new Date(end.getTime());
            } 
            startAggregation(context, startDate, endDate);
 
            if (yearMonth == "all")
               System.out.println("PROCESSING all open events (greater than field closed in stats.control) - from " + startDate + " to " + endDate);
            else
               System.out.println("PROCESSING " + yearMonth + " - from " + startDate + " to " + endDate);

            if (isVerbose)
                System.out.println("PROCESSING tables: " + tablesLine);

            for (EventTable table : eventTables)
            {
                if (isVerbose)
                    System.out.println("Processing table " + table.getTable());

                if (clean)
                {
                    for (Aggregation a : table.getAggregations()) 
                    {
                        if (isVerbose)
                            System.out.println("      " + new Time(new java.util.Date().getTime()).toString() + 
                                               "\tCleaning " + a.getDestination());
                        a.clean(context);
                    }
                }

                
                if (aggregate)
                {
                    if (!spiders.equals("none"))
                    {
                       if (isVerbose)
                           System.out.println("      " + new Time(new java.util.Date().getTime()).toString() +
                                              "\tFlagging spiders ");
                       table.flagSpiders(context, spiders);
                    }

                    for (Aggregation a : table.getAggregations()) 
                    {
                        if (isVerbose)
                            System.out.println("      " + new Time(new java.util.Date().getTime()).toString() + 
                                               "\tAggregating " + a.getDestination());
                        a.aggregate(context);
                    }
                }
                
                if (isVerbose)
                    System.out.println("   End Processing table " + table.getTable());

            }            
            finishAggregation(context); 
            context.commit();
        }
        catch (SQLException e)
        {
           System.out.println("Database error: " + e.getMessage());
           System.exit(1);
        }
        finally
        {
           if ((context != null) && context.isValid())
              context.abort();
        }                
        
        if (isVerbose)
            System.out.println("END PROCESSING ");

    }

    private static boolean aggregationOngoing(Context context) throws SQLException
    {
        boolean ret = true;

        String sql = "select * from stats.control";

        TableRowIterator iterator = DatabaseManager.query(context, sql);
        if (iterator.hasNext())
        {
            TableRow row = iterator.next();
            java.util.Date start = row.getDateColumn("agg_start");
            if (start != null)
               ret = true;
            else
               ret = false;
        }
        return ret;
    }

    private static Date closedDate(Context context) throws SQLException
    {
        Date ret = null;

        String sql = "select * from stats.control";

        TableRowIterator iterator = DatabaseManager.query(context, sql);
        if (iterator.hasNext())
        {
            TableRow row = iterator.next();
            ret = new Date(row.getDateColumn("closed").getTime());
        }
        return ret;
    }

    private static void startAggregation(Context context, Date start, Date end) throws SQLException
    {
            String updateSQL = "update stats.control set agg_start = ?, agg_end = ?";
            Object[] params = new Object[2];
            params[0] = start;
            params[1] = end;
            DatabaseManager.updateQuery(context, updateSQL, params);       
    }

    private static void finishAggregation(Context context) throws SQLException
    {
            String updateSQL = "update stats.control set agg_start = null, agg_end = null";
            DatabaseManager.updateQuery(context, updateSQL);
    }
 
    private static void initAggregations()
    {
        EventTable table;
        Aggregation agg;

        for (int i = 0; i < tables.size(); i++)
        {
            if (tables.get(i).equals("view"))
            {                   
                table = new EventTable("stats.view");
                
                agg = new Aggregation("stats.z_view_unagg_month", "stats.view_month");
                agg.addValueColumn("value", Types.BIGINT);                
                table.addAggregation(agg);
                
                agg = new Aggregation("stats.z_view_unagg_country_month", "stats.view_country_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                table.addAggregation(agg);
                
                agg = new Aggregation("stats.z_view_unagg_item_month", "stats.view_item_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                table.addAggregation(agg);
                
                agg = new Aggregation("stats.z_view_unagg_comm_month", "stats.view_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_coll_month", "stats.view_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_country_comm_month", "stats.view_country_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_country_coll_month", "stats.view_country_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_item_comm_month", "stats.view_item_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_item_coll_month", "stats.view_item_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_month_1", "stats.view_metadata_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_month_2", "stats.view_metadata_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_comm_month_1", "stats.view_metadata_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_comm_month_2", "stats.view_metadata_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_coll_month_1", "stats.view_metadata_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_view_unagg_metadata_coll_month_2", "stats.view_metadata_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);
                
                eventTables.add(table);
            }
            if (tables.get(i).equals("download"))
            {
                table = new EventTable("stats.download");
                
                agg = new Aggregation("stats.z_download_unagg_month", "stats.download_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_country_month", "stats.download_country_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                table.addAggregation(agg);
                
                agg = new Aggregation("stats.z_download_unagg_item_month", "stats.download_item_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                table.addAggregation(agg);
                
                agg = new Aggregation("stats.z_download_unagg_comm_month", "stats.download_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_coll_month", "stats.download_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_country_comm_month", "stats.download_country_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_country_coll_month", "stats.download_country_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("country_code", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_item_comm_month", "stats.download_item_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_item_coll_month", "stats.download_item_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("item_id", Types.INTEGER);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_month_1", "stats.download_metadata_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_month_2", "stats.download_metadata_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_comm_month_1", "stats.download_metadata_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_comm_month_2", "stats.download_metadata_comm_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("community_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_coll_month_1", "stats.download_metadata_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);

                agg = new Aggregation("stats.z_download_unagg_metadata_coll_month_2", "stats.download_metadata_coll_month");
                agg.addValueColumn("value", Types.BIGINT);
                agg.addValueColumn("relative_value", Types.DOUBLE);
                agg.addAggregationColumn("field_id", Types.INTEGER);
                agg.addAggregationColumn("field_value", Types.VARCHAR);
                agg.addAggregationColumn("collection_id", Types.INTEGER);
                table.addAggregation(agg);
                
                eventTables.add(table);
            }
        }        
    }
        
    private static Options setCommandLineOptions()
    {
        
        // create an options object and populate it
        Options options = new Options();
             
        OptionBuilder.withLongOpt("tables");
        OptionBuilder.withValueSeparator(',');
        OptionBuilder.withDescription(
                       "Run the clean and/or aggregation for the \n specified table(s).\n" +
                       "Possible values are:\n all, view, download\n" + 
                       "Separate multiple with a comma (,)\n" +
                       "Default is all");
           
        Option tables = OptionBuilder.create('t');
        tables.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(tables);    


        Option yearmonth = OptionBuilder.withArgName( "yearmonth" )
                                .hasArg()
                                .withDescription( "YearMonth to perform aggregation (default - all not closed) - format: yyyymm")
                                .create( "d" );
        options.addOption(yearmonth);     

        Option spiders = OptionBuilder.withArgName( "processing_mode" )
                                .hasArg()
                                .withDescription( "Process spiders.\nPossible values are:\nnotclosed, all, none.\nDefault is notclosed")
                                .create( "s" );
        options.addOption(yearmonth);

        options.addOption("a", "aggregate", false, "aggregate the unaggregated tables");
        options.addOption(spiders);
        options.addOption("c", "clean", false, "clean the current aggregation");
        options.addOption("r", "recalculate", false, "recalculate counters");
        options.addOption("p", "parameter", true, "Counters parameter");
        options.addOption("v", "verbose", false, "print aggregation logging to STDOUT");
        options.addOption("w", "workflow", false, "agreggates workflow intervals");
        options.addOption("h", "help", false, "help");
        
        return options;
    }
    
    private static void readCommandLineOptions(String[] argv)
    {
        // set up command line parser
        CommandLineParser parser = new PosixParser();
        CommandLine line = null;

        Options options = setCommandLineOptions();
        
        try
        {
            line = parser.parse(options, argv);
        }
        catch(MissingArgumentException e)
        {
            System.out.println("Missing Argument: " + e.getMessage());
            new HelpFormatter().printHelp("StatsAggregator\n", options);
            System.exit(1);
        }          
        catch (ParseException e)
        {
            System.out.println("ERROR: " + e.getMessage());
            new HelpFormatter().printHelp("StatsAggregator\n", options);
            System.exit(1);
        }

        if (line.hasOption('h'))
        {
            new HelpFormatter().printHelp("StatsAggregator\n", options);
            System.exit(0);
        }

        if (line.hasOption('d'))
        {
           yearMonth = line.getOptionValue("d");
        }

        if (!line.hasOption('a') && !line.hasOption('c'))
        {
            System.out.println("You have to specify if you want to \n" +
                               "clean the current aggregation or \n" +
                               "aggregate the unaggregated tables (or both).");
            new HelpFormatter().printHelp("StatsAggregator\n", options);
            System.exit(0);
        }
        
        if (line.hasOption('c') && line.hasOption('d'))
        {
           System.out.println("Parameter d is valid only in aggregation mode.\n With c parameter all aggregations will be deleted.");
           new HelpFormatter().printHelp("StatsAggregator\n", options);
           System.exit(0);
        }

        if (line.hasOption('w')) workflow = true;

        if (line.hasOption('v'))
        {
            isVerbose = true;
        }

        if (line.hasOption('s'))
        {
            spiders = line.getOptionValue("s");
            if (!(spiders.equals("none") || spiders.equals("notclosed") || spiders.equals("all")))
            {
               System.out.println("Parameter -s not valid");
               new HelpFormatter().printHelp("StatsAggregator\n", options);
               System.exit(0);
            }
        }

        if (line.hasOption('c'))
        {
            clean = true;
        }

        if (line.hasOption('a'))
        {
            aggregate = true;
        }
        
        if (line.hasOption('r')) {
        	recalculate = true;
        }
        
        if (line.hasOption('p')) {
        	counters = line.getOptionValue('p');
        }
        
        String temp[] = null;
        if(line.hasOption('t'))
        {
            temp = line.getOptionValues('t');                    
        }
        if(temp==null || temp.length==0)
        {   
            tables.add("view");
            tables.add("download");
        }
        else
        {
            for (int i = 0; i < temp.length; i++)
            {
                if (temp[i].equals("all"))
                {
                    tables.add("view");
                    tables.add("download");
                    break;
                }
                if (temp[i].equals("view"))
                    tables.add("view");
                else 
                    if (temp[i].equals("download"))
                        tables.add("download");
            }
        }
        tablesLine = "";
        for (int i = 0; i < tables.size(); i++)
            tablesLine = tablesLine + tables.get(i) + (i == tables.size() - 1 ? "" : ", ");
    }
}
