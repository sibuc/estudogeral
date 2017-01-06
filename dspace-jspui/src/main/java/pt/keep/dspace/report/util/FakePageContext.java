package pt.keep.dspace.report.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.BodyContent;

public class FakePageContext extends PageContext {

    public FakePageContext () {
        this.factory = JspFactory.getDefaultFactory();
    }

    public void initialize(Servlet servlet, ServletRequest request,
                           ServletResponse response, String errorPageURL,
                           boolean needsSession, int bufferSize,
                           boolean autoFlush)
        throws IOException, IllegalStateException, IllegalArgumentException
    {
	// 	 	InitAction ia=new InitAction( this, servlet, request, response,
	// 	 					  errorPageURL, needsSession, bufferSize,
	// 	 					  autoFlush);
	// 		java.security.AccessController.doPrivileged( ia );
	_initialize(servlet, request, response, errorPageURL, needsSession, bufferSize, autoFlush);
    }

    void _initialize(Servlet servlet, ServletRequest request,
                           ServletResponse response, String errorPageURL,
                           boolean needsSession, int bufferSize,
                           boolean autoFlush)
        throws IOException, IllegalStateException, IllegalArgumentException
    {

	// initialize state

	this.servlet      = servlet;
	this.config	  = servlet.getServletConfig();
	this.context	  = config.getServletContext();
	this.needsSession = needsSession;
	this.errorPageURL = errorPageURL;
	this.bufferSize   = bufferSize;
	this.autoFlush    = autoFlush;
	this.request      = request;
	this.response     = response;

	// setup session (if required)
	if (request instanceof HttpServletRequest && needsSession)
	    this.session = ((HttpServletRequest)request).getSession();

	if (needsSession && session == null)
	    throw new IllegalStateException("Page needs a session and none is available");

	// initialize the initial out ...
	//	System.out.println("Initialize PageContextImpl " + out );
	if( out == null ) {
	    out = _createOut(bufferSize, autoFlush); // throws
	} else
	    ((JspWriterImpl)out).init(response, bufferSize, autoFlush );
	
	if (this.out == null)
	    throw new IllegalStateException("failed initialize JspWriter");

	// register names/values as per spec

	setAttribute(OUT,         this.out);
	setAttribute(REQUEST,     request);
	setAttribute(RESPONSE,    response);

	if (session != null)
	    setAttribute(SESSION, session);

	setAttribute(PAGE,        servlet);
	setAttribute(CONFIG,      config);
	setAttribute(PAGECONTEXT, this);
	setAttribute(APPLICATION,  context);
    }

    public void release() {
	servlet      = null;
	config	     = null;
	context	     = null;
	needsSession = false;
	errorPageURL = null;
	bufferSize   = JspWriter.DEFAULT_BUFFER;
	autoFlush    = true;
	request      = null;
	response     = null;
	// Reuse // XXX problems - need to fix them first!!
	out	     = null; // out is closed elsewhere
	if( out instanceof JspWriterImpl )
	    ((JspWriterImpl)out).recycle();
	session      = null;

	attributes.clear();
    }

    public Object getAttribute(String name) {
	return attributes.get(name);
    }


    public Object getAttribute(String name, int scope) {
	switch (scope) {
	    case PAGE_SCOPE:
		return attributes.get(name);

	    case REQUEST_SCOPE:
		return request.getAttribute(name);

	    case SESSION_SCOPE:
		if (session == null)
		    throw new IllegalArgumentException("can't access SESSION_SCOPE without an HttpSession");
		else
		    return session.getAttribute(name);

	    case APPLICATION_SCOPE:
		return context.getAttribute(name);

	    default:
		throw new IllegalArgumentException("unidentified scope");
	}
    }


    public void setAttribute(String name, Object attribute) {
	attributes.put(name, attribute);
    }


    public void setAttribute(String name, Object o, int scope) {
	switch (scope) {
	    case PAGE_SCOPE:
		attributes.put(name, o);
	    break;

	    case REQUEST_SCOPE:
		request.setAttribute(name, o);
	    break;

	    case SESSION_SCOPE:
		if (session == null)
		    throw new IllegalArgumentException("can't access SESSION_SCOPE without an HttpSession");
		else
		    session.setAttribute(name, o);
	    break;

	    case APPLICATION_SCOPE:
		context.setAttribute(name, o);
	    break;

	    default:
		throw new IllegalArgumentException( "Invalid scope " + scope );
	}
    }

    public void removeAttribute(String name, int scope) {
	switch (scope) {
	    case PAGE_SCOPE:
		attributes.remove(name);
	    break;

	    case REQUEST_SCOPE:
		request.removeAttribute(name);

	    case SESSION_SCOPE:
		if (session == null)
		    throw new IllegalArgumentException("can't access SESSION_SCOPE without an HttpSession");
		else
                    session.removeAttribute(name);
                // was:
                //		    session.removeValue(name);
                // REVISIT Verify this is correct - akv
	    break;

	    case APPLICATION_SCOPE:
		context.removeAttribute(name);
	    break;

	    default:
		throw new IllegalArgumentException( "Invalid scope " + scope );
	}
    }

    public int getAttributesScope(String name) {
	if (attributes.get(name) != null) return PAGE_SCOPE;

	if (request.getAttribute(name) != null)
	    return REQUEST_SCOPE;

	if (session != null) {
	    if (session.getAttribute(name) != null)
	        return SESSION_SCOPE;
	}

	if (context.getAttribute(name) != null) return APPLICATION_SCOPE;

	return 0;
    }

    public Object findAttribute(String name) {
        Object o = attributes.get(name);
        if (o != null)
            return o;

        o = request.getAttribute(name);
        if (o != null)
            return o;

        if (session != null) {
            o = session.getAttribute(name);
            if (o != null)
                return o;
        }

        return context.getAttribute(name);
    }


    public Enumeration getAttributeNamesInScope(int scope) {
	switch (scope) {
	    case PAGE_SCOPE:
		return attributes.keys();

	    case REQUEST_SCOPE:
		return request.getAttributeNames();

	    case SESSION_SCOPE:
		if (session != null) {
		    return session.getAttributeNames();
		} else
		    throw new IllegalArgumentException("can't access SESSION_SCOPE without an HttpSession");

	    case APPLICATION_SCOPE:
		return context.getAttributeNames();

	    default: return new Enumeration() { // empty enumeration
		public boolean hasMoreElements() { return false; }

		public Object nextElement() { throw new NoSuchElementException(); }
	    };
	}
    }

    public void removeAttribute(String name) {
	try {
	    removeAttribute(name, PAGE_SCOPE);
	    removeAttribute(name, REQUEST_SCOPE);
	    if( session != null ) {
		removeAttribute(name, SESSION_SCOPE);
	    }
	    removeAttribute(name, APPLICATION_SCOPE);
	} catch (Exception ex) {
	    // we remove as much as we can, and
	    // simply ignore possible exceptions
	}
    }

    public JspWriter getOut() {
	return out;
    }

    public HttpSession getSession() { return session; }
    public Servlet getServlet() { return servlet; }
    public ServletConfig getServletConfig() { return config; }
    public ServletContext getServletContext() {
	return config.getServletContext();
    }
    public ServletRequest getRequest() { return request; }
    public ServletResponse getResponse() { return response; }
    public Exception getException() { return (Exception)request.getAttribute(EXCEPTION); }
    public Object getPage() { return servlet; }


    private final String getAbsolutePathRelativeToContext(String relativeUrlPath) {
        String path = relativeUrlPath;

        if (!path.startsWith("/")) {
	    String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
	    if (uri == null)
		uri = ((HttpServletRequest) request).getServletPath();
            String baseURI = uri.substring(0, uri.lastIndexOf('/'));
            path = baseURI+'/'+path;
        }

        return path;
    }

    public void include(String relativeUrlPath)
        throws ServletException, IOException
    {
        String path = getAbsolutePathRelativeToContext(relativeUrlPath);
        out.flush();
        context.getRequestDispatcher(path).include(request, response);
    }

    public void forward(String relativeUrlPath)
        throws ServletException, IOException
    {
        String path = getAbsolutePathRelativeToContext(relativeUrlPath);
	if( out!=null ) {
	    out.clearBuffer();
	}
        context.getRequestDispatcher(path).forward(request, response);
    }

    Stack writerStack = new Stack();

    public BodyContent pushBody() {
        JspWriter previous = out;
        writerStack.push(out);
        out = new BodyContentImpl(previous);
        return (BodyContent) out;
    }

    public JspWriter popBody() {
        out = (JspWriter) writerStack.pop();
        return out;
    }

    public void handlePageException(Exception e)
    throws IOException, ServletException {

	// set the request attribute with the exception.
	request.setAttribute("javax.servlet.jsp.jspException", e);

	if (errorPageURL != null && !errorPageURL.equals("")) {
            try {
                forward(errorPageURL);
            } catch (IllegalStateException ise) {
                include(errorPageURL);
            }
	} // Otherwise throw the exception wrapped inside a ServletException.
	else {
	    // Set the exception as the root cause in the ServletException
	    // to get a stack trace for the real problem
	    if( e instanceof IOException )
		throw (IOException)e;
	    if( e instanceof ServletException )
		throw (ServletException) e;
	    throw new ServletException(e);
	}

    }

    protected JspWriter _createOut(int bufferSize, boolean autoFlush)
        throws IOException, IllegalArgumentException
    {
	// This may fail for security expcetions, if the sandbox is broken !!
	//	try {
	return new JspWriterImpl(response, bufferSize, autoFlush);
	// 	} catch( Throwable t ) {
	// 	    loghelper.log("creating out", t);
	// 	    return null;
	// 	}
    }

    /*
     * fields
     */

    // per Servlet state

    protected 	        Servlet         servlet;
    protected 		ServletConfig   config;
    protected 		ServletContext  context;

    protected 		JspFactory	factory;

    protected		boolean		needsSession;

    protected		String		errorPageURL;

    protected		boolean		autoFlush;
    protected		int		bufferSize;

    // page scope attributes

    protected transient Hashtable	attributes = new Hashtable(16);

    // per request state

    protected transient ServletRequest	request;
    protected transient ServletResponse response;
    protected transient Object          page;

    protected transient HttpSession	session;

    // initial output stream

    protected transient JspWriter	out;

	@Override
	public void handlePageException(Throwable arg0) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		
	}

	public void include(String arg0, boolean arg1) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExpressionEvaluator getExpressionEvaluator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariableResolver getVariableResolver() {
		// TODO Auto-generated method stub
		return null;
	}
	//@Override
	public javax.el.ELContext getELContext() {
		// TODO Auto-generated method stub
		return null;
	}
}
