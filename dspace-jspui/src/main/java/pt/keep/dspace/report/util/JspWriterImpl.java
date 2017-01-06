package pt.keep.dspace.report.util;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspWriter;

/**
 * Write text to a character-output stream, buffering characters so as
 * to provide for the efficient writing of single characters, arrays,
 * and strings. 
 *
 * Provide support for discarding for the output that has been 
 * buffered. 
 * 
 * This needs revisiting when the buffering problems in the JSP spec
 * are fixed -akv 
 *
 * @author Anil K. Vijendran
 */
public class JspWriterImpl extends JspWriter {

    
    protected Writer out;

    protected ServletResponse response;
    
    protected char cb[];
    protected int nextChar;

    protected static int defaultCharBufferSize = Constants.DEFAULT_BUFFER_SIZE;

    protected boolean flushed = false;

    public JspWriterImpl() {
	super( defaultCharBufferSize, true );
    }

    /**
     * Create a buffered character-output stream that uses a default-sized
     * output buffer.
     *
     * @param  response  A Servlet Response
     */
    public JspWriterImpl(ServletResponse response) {
        this(response, defaultCharBufferSize, true);
    }

    /**
     * Create a new buffered character-output stream that uses an output
     * buffer of the given size.
     *
     * @param  response A Servlet Response
     * @param  sz   	Output-buffer size, a positive integer
     *
     * @exception  IllegalArgumentException  If sz is <= 0
     */
    public JspWriterImpl(ServletResponse response, int sz, 
                         boolean autoFlush) {
        super(sz, autoFlush);
        if (sz < 0)
            throw new IllegalArgumentException("Buffer size <= 0");
	this.response = response;
        cb = sz == 0 ? null : new char[sz];
	nextChar = 0;
    }

    void init( ServletResponse response, int sz, boolean autoFlush ) {
	this.response= response;
	if( sz > 0 && ( cb == null || sz > cb.length ) )
	    cb=new char[sz];
	nextChar = 0;
	this.autoFlush=autoFlush;
	this.bufferSize=sz;
    }

    /**
     * Flush the output buffer to the underlying character stream, without
     * flushing the stream itself.  This method is non-private only so that it
     * may be invoked by PrintStream.
     */
    public final void flushBuffer() throws IOException {
	synchronized (lock) {
            if (bufferSize == 0)
                return;
            flushed = true;
	    ensureOpen();
	    if (nextChar == 0)
		return;
            initOut();
            out.write(cb, 0, nextChar);
	    nextChar = 0;
	}
    }

    protected void initOut() throws IOException {
        if (out == null) {
            out = response.getWriter();
	    //System.out.println("JspWriterImpl: initOut: " + this + " " +out);
	}
    }
	

    /**
     * Discard the output buffer.
     */
    public final void clear() throws IOException {
	synchronized (lock) {
            if (bufferSize == 0)
                throw new IllegalStateException("");
            if (flushed)
                throw new IOException("");
            ensureOpen();
	    nextChar = 0;
	}
    }

    public void clearBuffer() throws IOException {
	synchronized (lock) {
            if (bufferSize == 0)
                throw new IllegalStateException("");
            ensureOpen();
	    nextChar = 0;
	}
    }

    private final void bufferOverflow() throws IOException {
        throw new IOException("");
    }

    /**
     * Flush the stream.
     *
     */
    public void flush()  throws IOException {
        synchronized (lock) {
            flushBuffer();
            if (out != null) {
                out.flush();
		// Also flush the response buffer.
		response.flushBuffer();
	    }
        }
    }

    /**
     * Close the stream.
     *
     */
    public void close() throws IOException {
        synchronized (lock) {
            if (response == null)
                return;
            flush();
            if (out != null)
                out.close();
            out = null;
	    //            cb = null;
        }
    }

    /**
     * @return the number of bytes unused in the buffer
     */
    public int getRemaining() {
        return bufferSize - nextChar;
    }

    /** check to make sure that the stream has not been closed */
    protected void ensureOpen() throws IOException {
	if (response == null)
	    throw new IOException("Stream closed");
    }


    /**
     * Write a single character.
     *
     */
    public void write(int c) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (bufferSize == 0) {
                initOut();
                out.write(c);
            }
            else {
                if (nextChar >= bufferSize)
                    if (autoFlush)
                        flushBuffer();
                    else
                        bufferOverflow();
                cb[nextChar++] = (char) c;
            }
        }
    }

    /**
     * Our own little min method, to avoid loading java.lang.Math if we've run
     * out of file descriptors and we're trying to print a stack trace.
     */
    private int min(int a, int b) {
	if (a < b) return a;
	return b;
    }

    /**
     * Write a portion of an array of characters.
     *
     * 

Ordinarily this method stores characters from the given array into * this stream's buffer, flushing the buffer to the underlying stream as * needed. If the requested length is at least as large as the buffer, * however, then this method will flush the buffer and write the characters * directly to the underlying stream. Thus redundant * DiscardableBufferedWriters will not copy data unnecessarily. * * @param cbuf A character array * @param off Offset from which to start reading characters * @param len Number of characters to write * */ public void write(char cbuf[], int off, int len) throws IOException { synchronized (lock) { ensureOpen(); if (bufferSize == 0) { initOut(); out.write(cbuf, off, len); return; } if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) { throw new IndexOutOfBoundsException(); } else if (len == 0) { return; } if (len >= bufferSize) { /* If the request length exceeds the size of the output buffer, flush the buffer and then write the data directly. In this way buffered streams will cascade harmlessly. */ if (autoFlush) flushBuffer(); else bufferOverflow(); initOut(); out.write(cbuf, off, len); return; } int b = off, t = off + len; while (b < t) { int d = min(bufferSize - nextChar, t - b); System.arraycopy(cbuf, b, cb, nextChar, d); b += d; nextChar += d; if (nextChar >= bufferSize) if (autoFlush) flushBuffer(); else bufferOverflow(); } } } /** * Write an array of characters. This method cannot be inherited from the * Writer class because it must suppress I/O exceptions. */ public void write(char buf[]) throws IOException { write(buf, 0, buf.length); } /** * Write a portion of a String. * * @param s String to be written * @param off Offset from which to start reading characters * @param len Number of characters to be written * */ public void write(String s, int off, int len) throws IOException { synchronized (lock) { ensureOpen(); if (bufferSize == 0) { initOut(); out.write(s, off, len); return; } int b = off, t = off + len; while (b < t) { int d = min(bufferSize - nextChar, t - b); s.getChars(b, b + d, cb, nextChar); b += d; nextChar += d; if (nextChar >= bufferSize) if (autoFlush) flushBuffer(); else bufferOverflow(); } } } /** * Write a string. This method cannot be inherited from the Writer class * because it must suppress I/O exceptions. */ public void write(String s) throws IOException { write(s, 0, s.length()); } static String lineSeparator; static { try { lineSeparator = System.getProperty("line.separator"); } catch( RuntimeException ex ) { lineSeparator="\r\n"; } } /** * Write a line separator. The line separator string is defined by the * system property line.separator, and is not necessarily a single * newline ('\n') character. * * @exception IOException If an I/O error occurs */ public void newLine() throws IOException { synchronized (lock) { write(lineSeparator); } } /* Methods that do not terminate lines */ /** * Print a boolean value. The string produced by {@link * java.lang.String#valueOf(boolean)} is translated into bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link * #write(int)} method. * * @param b The boolean to be printed */ public void print(boolean b) throws IOException { write(b ? "true" : "false"); } /** * Print a character. The character is translated into one or more bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link * #write(int)} method. * * @param c The char to be printed */ public void print(char c) throws IOException { write(String.valueOf(c)); } /** * Print an integer. The string produced by {@link * java.lang.String#valueOf(int)} is translated into bytes according * to the platform's default character encoding, and these bytes are * written in exactly the manner of the {@link #write(int)} * method. * * @param i The int to be printed * @see java.lang.Integer#toString(int) */ public void print(int i) throws IOException { write(String.valueOf(i)); } /** * Print a long integer. The string produced by {@link * java.lang.String#valueOf(long)} is translated into bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link #write(int)} * method. * * @param l The long to be printed * @see java.lang.Long#toString(long) */ public void print(long l) throws IOException { write(String.valueOf(l)); } /** * Print a floating-point number. The string produced by {@link * java.lang.String#valueOf(float)} is translated into bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link #write(int)} * method. * * @param f The float to be printed * @see java.lang.Float#toString(float) */ public void print(float f) throws IOException { write(String.valueOf(f)); } /** * Print a double-precision floating-point number. The string produced by * {@link java.lang.String#valueOf(double)} is translated into * bytes according to the platform's default character encoding, and these * bytes are written in exactly the manner of the {@link * #write(int)} method. * * @param d The double to be printed * @see java.lang.Double#toString(double) */ public void print(double d) throws IOException { write(String.valueOf(d)); } /** * Print an array of characters. The characters are converted into bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link #write(int)} * method. * * @param s The array of chars to be printed * * @throws NullPointerException If s is null */ public void print(char s[]) throws IOException { write(s); } /** * Print a string. If the argument is null then the string * "null" is printed. Otherwise, the string's characters are * converted into bytes according to the platform's default character * encoding, and these bytes are written in exactly the manner of the * {@link #write(int)} method. * * @param s The String to be printed */ public void print(String s) throws IOException { if (s == null) { s = "null"; } write(s); } /** * Print an object. The string produced by the {@link * java.lang.String#valueOf(Object)} method is translated into bytes * according to the platform's default character encoding, and these bytes * are written in exactly the manner of the {@link #write(int)} * method. * * @param obj The Object to be printed * @see java.lang.Object#toString() */ public void print(Object obj) throws IOException { write(String.valueOf(obj)); } /* Methods that do terminate lines */ /** * Terminate the current line by writing the line separator string. The * line separator string is defined by the system property * line.separator, and is not necessarily a single newline * character ('\n'). * * Need to change this from PrintWriter because the default * println() writes to the sink directly instead of through the * write method... */ public void println() throws IOException { newLine(); } /** * Print a boolean value and then terminate the line. This method behaves * as though it invokes {@link #print(boolean)} and then * {@link #println()}. */ public void println(boolean x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print a character and then terminate the line. This method behaves as * though it invokes {@link #print(char)} and then {@link * #println()}. */ public void println(char x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print an integer and then terminate the line. This method behaves as * though it invokes {@link #print(int)} and then {@link * #println()}. */ public void println(int x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print a long integer and then terminate the line. This method behaves * as though it invokes {@link #print(long)} and then * {@link #println()}. */ public void println(long x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print a floating-point number and then terminate the line. This method * behaves as though it invokes {@link #print(float)} and then * {@link #println()}. */ public void println(float x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print a double-precision floating-point number and then terminate the * line. This method behaves as though it invokes {@link * #print(double)} and then {@link #println()}. */ public void println(double x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print an array of characters and then terminate the line. This method * behaves as though it invokes {@link #print(char[])} and then * {@link #println()}. */ public void println(char x[]) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print a String and then terminate the line. This method behaves as * though it invokes {@link #print(String)} and then * {@link #println()}. */ public void println(String x) throws IOException { synchronized (lock) { print(x); println(); } } /** * Print an Object and then terminate the line. This method behaves as * though it invokes {@link #print(Object)} and then * {@link #println()}. */ public void println(Object x) throws IOException { synchronized (lock) { print(x); println(); } } /** Package-level access */ void recycle() { flushed = false; nextChar = 0; } } 
