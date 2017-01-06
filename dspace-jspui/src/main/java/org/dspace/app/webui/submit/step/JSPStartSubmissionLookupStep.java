/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.webui.submit.step;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dspace.app.util.SubmissionInfo;
import org.dspace.core.ConfigurationManager;
import org.dspace.app.webui.submit.JSPStep;
import org.dspace.app.webui.submit.JSPStepManager;
import org.dspace.app.webui.util.JSPManager;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.submit.lookup.SubmissionLookupDataLoader;
import org.dspace.submit.lookup.SubmissionLookupService;
import org.dspace.submit.step.StartSubmissionLookupStep;
import org.dspace.submit.step.DescribeStep;
import org.dspace.app.webui.submit.step.JSPDescribeStep;
import org.dspace.content.WorkspaceItem;
import static org.dspace.submit.AbstractProcessingStep.STATUS_COMPLETE;
import static org.dspace.submit.step.SelectCollectionStep.STATUS_INVALID_COLLECTION;
import org.dspace.utils.DSpace;

/**
 * Step which controls selecting an item from external database service to auto
 * fill metadata for DSpace JSP-UI
 * <P>
 * This JSPStep class works with the SubmissionController servlet for the JSP-UI
 * <P>
 * The following methods are called in this order:
 * <ul>
 * <li>Call doPreProcessing() method</li>
 * <li>If showJSP() was specified from doPreProcessing(), then the JSP specified
 * will be displayed</li>
 * <li>If showJSP() was not specified from doPreProcessing(), then the
 * doProcessing() method is called an the step completes immediately</li>
 * <li>Call doProcessing() method on appropriate AbstractProcessingStep after
 * the user returns from the JSP, in order to process the user input</li>
 * <li>Call doPostProcessing() method to determine if more user interaction is
 * required, and if further JSPs need to be called.</li>
 * <li>If there are more "pages" in this step then, the process begins again
 * (for the new page).</li>
 * <li>Once all pages are complete, control is forwarded back to the
 * SubmissionController, and the next step is called.</li>
 * </ul>
 * 
 * @see org.dspace.app.webui.servlet.SubmissionController
 * @see org.dspace.app.webui.submit.JSPStep
 * @see org.dspace.submit.step.StartSubmissionLookupStep
 * 
 * @author Andrea Bollini
 * @version $Revision$
 */
public class JSPStartSubmissionLookupStep extends JSPStep
{
    /** JSP which displays HTML for this Class * */
    private static final String START_LOOKUP_JSP = "/submit/start-lookup-submission.jsp";

    /** log4j logger */
    private static Logger log = Logger
            .getLogger(JSPStartSubmissionLookupStep.class);

    SubmissionLookupService slService = new DSpace().getServiceManager()
            .getServiceByName(SubmissionLookupService.class.getCanonicalName(),
                    SubmissionLookupService.class);

    /**
     * Do any pre-processing to determine which JSP (if any) is used to generate
     * the UI for this step. This method should include the gathering and
     * validating of all data required by the JSP. In addition, if the JSP
     * requires any variable to passed to it on the Request, this method should
     * set those variables.
     * <P>
     * If this step requires user interaction, then this method must call the
     * JSP to display, using the "showJSP()" method of the JSPStepManager class.
     * <P>
     * If this step doesn't require user interaction OR you are solely using
     * Manakin for your user interface, then this method may be left EMPTY,
     * since all step processing should occur in the doProcessing() method.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            current servlet request object
     * @param response
     *            current servlet response object
     * @param subInfo
     *            submission info object
     */
    public void doPreProcessing(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo)
            throws ServletException, IOException, SQLException,
            AuthorizeException
    {
        if (request.getAttribute("no.collection") == null
                || !(Boolean) request.getAttribute("no.collection"))
        {
            request.setAttribute("s_uuid", UUID.randomUUID().toString());
        }
        log.info("JSPStartSubmissionLookupStep - doPreProcessing");
        /*
         * Possible parameters from JSP:
         * 
         * collection= <collection_id> - a collection that has already been
         * selected (to use as preference! it is not the final choice!!!)
         * 
         * Na situacao da colecao "collection" ser relativa a provas de doutoramento
         * ou de agregação, avança um passo, assumindo essa coleção como 
         * default e passa a collectionid.
         * collectionid = the FINAL chosed collection!!!
         * 
         * With no parameters, this servlet prepares for display of the Select
         * Collection JSP.
         */
        int comm_id = 0;
        comm_id = ConfigurationManager.getIntProperty("comm.doutoramentos");
        log.info("comm_id:" + comm_id);
        String comm_doutoramentos = Community.find(context, comm_id).getHandle();
        log.info("comm_doutoramentos " + comm_doutoramentos);
        String comm_agregacao = Community.find(context, ConfigurationManager.getIntProperty("comm.agregacao")).getHandle();
        log.info("comm_agregacao " + comm_agregacao);
        
        int collection_id = UIUtil.getIntParameter(request, "collection");
        
        log.info("collection from calling page - " + collection_id);
        int collectionID = UIUtil.getIntParameter(request, "collectionid");
        log.info("collectionid from calling page - " + collectionID);
        Collection col = null;
        Community com =  null;
        boolean provas_academicas = false;
        /* Para contemplar a situação das provas académicas */
        if (collection_id != -1) {
            
            col = Collection.find(context, collection_id);
            com = (Community) col.getParentObject();
            String handle = com.getHandle();
            if (!handle.matches(comm_doutoramentos) &&  !handle.matches(comm_agregacao)) {
                log.info("Não são provas académicas");
                col = null;
            }
            else {
                log.info("Provas académicas");
                provas_academicas = true;
                // acrescentado 21/12--- 
                collectionID = collection_id;
                // fim acrescentado
                col = Collection.find(context, collection_id);
              if (col != null)
              {
            // create our new Workspace Item
            WorkspaceItem wi = WorkspaceItem.create(context, col, true);

            // update Submission Information with this Workspace Item
            subInfo.setSubmissionItem(wi);

            // commit changes to database
            context.commit();
                // request.setAttribute("collectionid", collectionID);
                request.setAttribute("collectionid", collection_id);
                request.setAttribute("collection_id", collection_id);
                // request.setAttribute("collectionID", collectionID);
                request.setAttribute("collectionID", collection_id);
            // need to reload current submission process config,

            request.setAttribute("suuid", UUID.randomUUID().toString());
            log.info("Provas académicas - collectionID - " + collectionID);
            log.info("Current page " + StartSubmissionLookupStep.getCurrentPage(request));
            // since it is based on the Collection selected
            // subInfo.reloadSubmissionConfig(request);
        }

        // no errors occurred           
            
        }
        }
       if (!provas_academicas) { 
        if (collectionID != -1)
        {
            col = Collection.find(context, collectionID);
        }

        // if we already have a valid collection, then we can forward directly
        // to post-processing
        if (col != null)
        {
            log.info("Select Collection page skipped, since a Collection ID was already found.  Collection ID="
                    + collectionID);

        }
        else
        {
            // gather info for JSP page
            com = UIUtil.getCommunityLocation(request);

            Collection[] collections;

            if (com != null)
            {
                // In a community. Show collections in that community only.
                collections = Collection.findAuthorized(context, com,
                        Constants.ADD);
            }
            else
            {
                // Show all collections
                collections = Collection.findAuthorizedOptimized(context,
                        Constants.ADD);
            }

            // save collections to request for JSP
            request.setAttribute("collections", collections);
            request.setAttribute("collection_id", collection_id);
            request.setAttribute("collectionID", collectionID);

            Map<String, List<String>> identifiers2providers = slService
                    .getProvidersIdentifiersMap();
            List<String> searchProviders = slService.getSearchProviders();
            List<String> fileProviders = slService.getFileProviders();
            request.setAttribute("identifiers2providers", identifiers2providers);
            request.setAttribute("searchProviders", searchProviders);
            request.setAttribute("fileLoaders", fileProviders);
            request.setAttribute("identifiers", slService.getIdentifiers());
            // we need to load the select collection JSP
            log.info("Current page - antes call JSP" + StartSubmissionLookupStep.getCurrentPage(request));
            JSPStepManager
                    .showJSP(request, response, subInfo, START_LOOKUP_JSP);
        }
       }
    }

    /**
     * Do any post-processing after the step's backend processing occurred (in
     * the doProcessing() method).
     * <P>
     * It is this method's job to determine whether processing completed
     * successfully, or display another JSP informing the users of any potential
     * problems/errors.
     * <P>
     * If this step doesn't require user interaction OR you are solely using
     * Manakin for your user interface, then this method may be left EMPTY,
     * since all step processing should occur in the doProcessing() method.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            current servlet request object
     * @param response
     *            current servlet response object
     * @param subInfo
     *            submission info object
     * @param status
     *            any status/errors reported by doProcessing() method
     */
    public void doPostProcessing(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo, int status)
            throws ServletException, IOException, SQLException,
            AuthorizeException
    {
        // if the user didn't select a collection,
        // send him/her back to "select a collection" page
        log.info("doPostProcessing - currentpage - " + StartSubmissionLookupStep.getCurrentPage(request));
        if (status == StartSubmissionLookupStep.STATUS_NO_COLLECTION)
        {
            // specify "no collection" error message should be displayed
            log.info("Status_no_collection");
            request.setAttribute("no.collection", new Boolean(true));

            // reload this page, by re-calling doPreProcessing()
            doPreProcessing(context, request, response, subInfo);
        }
        else if (status == StartSubmissionLookupStep.STATUS_INVALID_COLLECTION)
        {
            log.info("Status_invalid_collection");
            JSPManager.showInvalidIDError(request, response,
                    request.getParameter("collectionid"), Constants.COLLECTION);
        }
        else if (status == StartSubmissionLookupStep.STATUS_NO_SUUID)
        {
            // specify "no suuid" error message should be displayed
            log.info("Status_no_suuid");
            request.setAttribute("no.suuid", new Boolean(true));
 
            // reload this page, by re-calling doPreProcessing()
            doPreProcessing(context, request, response, subInfo);
        }
        else if (status == StartSubmissionLookupStep.STATUS_SUBMISSION_EXPIRED)
        {
            // specify "no collection" error message should be displayed
            log.info("expired");
            request.setAttribute("expired", new Boolean(true));

            // reload this page, by re-calling doPreProcessing()
            doPreProcessing(context, request, response, subInfo);
        }
        else if (status != StartSubmissionLookupStep.STATUS_COMPLETE)
        {
            // specify "no suuid" error message should be displayed
            log.info("Status_complete");
            request.setAttribute("no.suuid", new Boolean(true));

            // reload this page, by re-calling doPreProcessing()
            doPreProcessing(context, request, response, subInfo);
        }
    }

    /**
     * Return the URL path (e.g. /submit/review-metadata.jsp) of the JSP which
     * will review the information that was gathered in this Step.
     * <P>
     * This Review JSP is loaded by the 'Verify' Step, in order to dynamically
     * generate a submission verification page consisting of the information
     * gathered in all the enabled submission steps.
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            current servlet request object
     * @param response
     *            current servlet response object
     * @param subInfo
     *            submission info object
     */
    public String getReviewJSP(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo)
    {
        return NO_JSP; // at this time, you cannot review what collection you
                       // selected.
    }
}
