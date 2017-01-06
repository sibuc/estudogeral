/*
 * JSPUploadStep.java
 *
 * Version: $Revision: 3705 $
 *
 * Date: $Date: 2009-04-11 19:02:24 +0200 (Sat, 11 Apr 2009) $
 *
 * Copyright (c) 2002-2005, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
package org.dspace.app.webui.submit.step;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.dspace.app.util.DCInputsReader;
import org.dspace.app.util.DCInputsReaderException;
import org.dspace.app.webui.submit.JSPStep;
import org.dspace.app.webui.submit.JSPStepManager;
import org.dspace.app.util.SubmissionInfo;
import org.dspace.app.webui.util.UIUtil;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.dspace.core.LogManager;
import org.dspace.submit.step.DeGoisStep;
import org.dspace.submit.step.UploadStep;

/**
 * Upload step for DSpace JSP-UI. Handles the pages that revolve around uploading files
 * (and verifying a successful upload) for an item being submitted into DSpace.
 * <P>
 * This JSPStep class works with the SubmissionController servlet
 * for the JSP-UI
 * <P>
 * The following methods are called in this order:
 * <ul>
 * <li>Call doPreProcessing() method</li>
 * <li>If showJSP() was specified from doPreProcessing(), then the JSP
 * specified will be displayed</li>
 * <li>If showJSP() was not specified from doPreProcessing(), then the
 * doProcessing() method is called an the step completes immediately</li>
 * <li>Call doProcessing() method on appropriate AbstractProcessingStep after the user returns from the JSP, in order
 * to process the user input</li>
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
 * @see org.dspace.submit.step.UploadStep
 * 
 * @author Tim Donohue
 * @version $Revision: 3705 $
 */
public class JSPDeGoisStep extends JSPStep
{
    /** JSP to show any upload errors * */
    private static final String DEGOIS_PAGE = "/submit/degois.jsp";
    private static final String DEGOIS_USER_ERROR_JSP = "/submit/degois-user-error.jsp";
    private static final String DEGOIS_UPLOAD_ERROR_JSP = "/submit/degois-upload-error.jsp";
    
    /** JSP to review uploaded files * */
    private static final String DEGOIS_SUCCESS_JSP = "/submit/degois-success.jsp";

    /** log4j logger */
    private static Logger log = Logger.getLogger(JSPUploadStep.class);

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
    	boolean active = ConfigurationManager.getBooleanProperty(DeGoisStep.CONFIG_ACTIVE, false);
    	if (active) {
	        // pass on the fileupload setting
	        if (subInfo != null)
	        {
	            Collection c = subInfo.getSubmissionItem().getCollection();
	            try{
	            	DCInputsReader inputsReader = new DCInputsReader();
	            	request.setAttribute("submission.inputs", inputsReader.getInputs(c
	                    .getHandle()));
	            }catch (DCInputsReaderException e) {
					log.error(e);
				}
	        }
	
	        // show whichever upload page is appropriate
	        // (based on if this item already has files or not)
	        showUploadPage(context, request, response, subInfo);
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
    	if (status == DeGoisStep.STATUS_SKIP) return; 
        String buttonPressed = UIUtil.getSubmitButton(request, DeGoisStep.SUBMIT_DEGOIS_REQUEST);
        
        // ------------------------------
        // Check for Errors!
        // ------------------------------
        // if an error or message was passed back, determine what to do!
        if (status != DeGoisStep.STATUS_COMPLETE)
        {
            if (status == DeGoisStep.STATUS_INVALID_USER)
            {
                // Some type of integrity error occurred
                log.warn(LogManager.getHeader(context, "integrity_error",
                        "Invalid user"));
                
                JSPStepManager.showJSP(request, response, subInfo, DEGOIS_USER_ERROR_JSP);
            }
            else if (status == DeGoisStep.STATUS_CANNOT_SEND)
            {
                // There was a problem uploading the file!
                JSPStepManager.showJSP(request, response, subInfo, DEGOIS_UPLOAD_ERROR_JSP);
            }
        }
        
        // As long as there are no errors, clicking Next
        // should immediately send them to the next step
        if (status == DeGoisStep.STATUS_COMPLETE )
        {
        	if (buttonPressed.equals(DeGoisStep.SUBMIT_DEGOIS_REQUEST))
        		return;
        }
        
        
        if (buttonPressed.equals(DeGoisStep.SUBMIT_SKIP_BUTTON))
        	return;
        
        showUploadPage(context, request, response, subInfo);
    }

    /**
     * Display the appropriate upload page in the file upload sequence. Which
     * page this is depends on whether the user has uploaded any files in this
     * item already.
     * 
     * @param context
     *            the DSpace context object
     * @param request
     *            the request object
     * @param response
     *            the response object
     * @param subInfo
     *            the SubmissionInfo object
     * @param justUploaded
     *            true, if the user just finished uploading a file
     */
    private void showUploadPage(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo) throws SQLException, ServletException,
            IOException
    {

            // show the page to choose a file to upload
            showChooseFile(context, request, response, subInfo);
    }

    /**
     * Show the page which allows the user to choose another file to upload
     * 
     * @param context
     *            current DSpace context
     * @param request
     *            the request object
     * @param response
     *            the response object
     * @param subInfo
     *            the SubmissionInfo object
     */
    private void showChooseFile(Context context, HttpServletRequest request,
            HttpServletResponse response, SubmissionInfo subInfo)
            throws SQLException, ServletException, IOException
    {

        // set to null the bitstream in subInfo, we need to process a new file
        // we don't need any info about previous files...
        subInfo.setBitstream(null);
        
        // load JSP which allows the user to select a file to upload
        JSPStepManager.showJSP(request, response, subInfo, DEGOIS_PAGE);
    }

	@Override
	public String getReviewJSP(Context context, HttpServletRequest request,
			HttpServletResponse response, SubmissionInfo subInfo) {
		return "/submit/review.jsp";
	}
}
