package pt.keep.dspace.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;
import org.jfree.util.Log;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.field.Field;
import pt.keep.dspace.report.field.FieldManager;
import pt.keep.dspace.report.generators.datasource.DSpaceItemBibDataSource;
import pt.keep.dspace.report.util.FakePageContext;
import pt.keep.dspace.report.util.ItemUtils;
import pt.keep.dspace.report.util.TranslateManager;

import com.lyncode.xoai.dataprovider.exceptions.MetadataBindException;
import com.lyncode.xoai.dataprovider.util.MarshallingUtils;
import com.lyncode.xoai.dataprovider.xml.xoai.Metadata;

public class BibTexGenerator {
	private static Logger log = LogManager.getLogger(BibTexGenerator.class);
	
	private PageContext _page;
	private ExportEntity _entity;
	private String _entityID;
	private String _order;
	private List<Field> _fields;
	private String _format;
	
	public BibTexGenerator(Context ct, Servlet servlet,
			HttpServletRequest request, HttpServletResponse response) throws ReportGenerationException {
		try {
			_page = new FakePageContext();
			_page.initialize(servlet, request, response, "", true, 20, true);
			TranslateManager.init(_page);
			
			_entity = ExportEntity.valueOf(this.getParameter(request, "type", "author").toUpperCase());
			_entityID = this.getParameter(request, "id", null);
			if (_entity == ExportEntity.AUTHOR)
				_entityID = new String(Base64.decodeBase64(_entityID.getBytes()));
			_order = this.getParameter(request, "order", "ASC");
			
			if (_entityID == null) throw new ReportGenerationException("Entity id cannot be null");
			
			_fields = FieldManager.getInstance(ct).getFields();
			
			for (Field f : _fields) 
				f.fill(request);
			
			_format = request.getParameter("format");
			if (_format == null) _format = "bibtex";
			
		} catch (IllegalStateException e) {
			throw new ReportGenerationException(e);
		} catch (IllegalArgumentException e) {
			throw new ReportGenerationException(e);
		} catch (IOException e) {
			throw new ReportGenerationException(e);
		} catch (ReportConfigurationException e) {
			throw new ReportGenerationException(e);
		} 
		
	}

	private String getParameter (HttpServletRequest request, String arg, String defaultvalue) {
    	String value = request.getParameter(arg);
    	if (value == null) return defaultvalue;
    	else return value;
    }
    
    public void export (Context ct, HttpServletResponse response) throws ReportGenerationException {
		
		try {
			

			ItemIterator iterator = DSpaceItemBibDataSource.query(ct, _entity, _entityID, _fields, _order);
			ServletOutputStream out = response.getOutputStream();

			String contentType = ConfigurationManager.getProperty("stats.reports.bib."+_format+".mimeType");
			String file = ConfigurationManager.getProperty("stats.reports.bib."+_format+".xslt");
			response.setContentType(contentType);
			response.setHeader("content-disposition", "inline; filename=\"bibtex.bib\"");


			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer schemaTransformer = tFactory.newTransformer(new StreamSource(new File(file)));
			
			while (iterator.hasNext()) {
				Item item = iterator.next();
				log.debug("Item: "+item.getID());
				
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				Metadata metadata = ItemUtils.retrieveMetadata(item);
				log.debug("Number of Metadata Elements: "+ metadata.getElement().size());
				
				MarshallingUtils.writeMetadata(output, metadata);
				
				output.flush();
				output.close();
				
				log.debug(output.toString());
				
				
				log.debug("BibTex XSLT: "+file);
				
				log.debug(schemaTransformer != null ? "NOT NULL" : "NULL");
				
				ByteArrayOutputStream inte = new ByteArrayOutputStream();
				InputStream in = new ByteArrayInputStream(output.toString().getBytes());
				StreamSource sts = new StreamSource(in);
				StreamResult srs = new StreamResult(inte);
				schemaTransformer.transform(sts, srs);
				out.write(inte.toString().getBytes());
			}
			
			out.flush();
			out.close();
			
		} catch (IOException e) {
			throw new ReportGenerationException(e);
		} catch (SQLException e) {
			throw new ReportGenerationException(e);
		} catch (MetadataBindException e) {
			throw new ReportGenerationException(e);
		} catch (TransformerConfigurationException e) {
			throw new ReportGenerationException(e);
		} catch (TransformerException e) {
			throw new ReportGenerationException(e);
		}
    }
    
}
