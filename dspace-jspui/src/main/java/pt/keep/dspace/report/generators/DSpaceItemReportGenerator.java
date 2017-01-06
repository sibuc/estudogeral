package pt.keep.dspace.report.generators;

//import static net.sf.dynamicreports.report.builder.DynamicReports.*; 

import java.awt.Color;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.field.Field;
import pt.keep.dspace.report.field.FieldManager;
import pt.keep.dspace.report.generators.datasource.DSpaceItemDataSource;
import pt.keep.dspace.report.util.TranslateManager;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.PageXofYBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.group.ColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.GroupHeaderLayout;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;

import net.sf.jasperreports.engine.JRDataSource;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

public class DSpaceItemReportGenerator extends AbstractReportGenerator {
    private static Logger log = Logger.getLogger(DSpaceItemReportGenerator.class);
    
	private ExportEntity _entity;
	private String _entityID;
	private String _order;
	
	private List<Field> _fields;
	
	private StyleBuilder rootStyle;
	private StyleBuilder boldStyle;
	private StyleBuilder italicStyle;
	private StyleBuilder boldCenteredStyle;
	private StyleBuilder bold12CenteredStyle;
	private StyleBuilder bold18CenteredStyle;
	private StyleBuilder bold22CenteredStyle;
	private StyleBuilder columnStyle;
	private StyleBuilder columnTitleStyle;
	private StyleBuilder groupStyle;
	private StyleBuilder subtotalStyle;
	private StyleBuilder crosstabGroupStyle;
	private StyleBuilder crosstabGroupTotalStyle;
	private StyleBuilder crosstabGrandTotalStyle;
	private StyleBuilder crosstabCellStyle;
	private PageXofYBuilder footerComponent;
	private ReportTemplateBuilder reportTemplate;
	
	public DSpaceItemReportGenerator(Context ct, Servlet servlet,
			HttpServletRequest request, HttpServletResponse response) throws ReportGenerationException {
		super(servlet, request, response);
		try {
			_entity = ExportEntity.valueOf(this.getParameter(request, "type", "author").toUpperCase());
			_entityID = this.getParameter(request, "id", null);
			if (_entity == ExportEntity.AUTHOR)
				_entityID = new String(Base64.decodeBase64(_entityID.getBytes()));
			_order = this.getParameter(request, "order", "ASC");
			
			if (_entityID == null) throw new ReportGenerationException("Entity id cannot be null");
			
			FieldManager manager = FieldManager.getInstance(ct);
			
			_fields = manager.getFields();
			for (Field f : _fields) 
				f.fill(request);
			
			this.initStyles();
			
		} catch (ReportConfigurationException e) {
			throw new ReportGenerationException(e);
		}
		
	}
	
	private void initStyles () throws ReportGenerationException {
		rootStyle = stl.style().setPadding(2);
		boldStyle = stl.style(rootStyle).bold();
		italicStyle = stl.style(rootStyle).italic();
		boldCenteredStyle = stl.style(boldStyle).setAlignment(
				HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
		bold12CenteredStyle = stl.style(boldCenteredStyle).setFontSize(12);
		bold18CenteredStyle = stl.style(boldCenteredStyle).setFontSize(18);
		bold22CenteredStyle = stl.style(boldCenteredStyle).setFontSize(22);
		columnStyle = stl.style(rootStyle).setVerticalAlignment(VerticalAlignment.MIDDLE);
		columnTitleStyle = stl.style(columnStyle)
				.setBorder(stl.pen1Point())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setBackgroundColor(Color.LIGHT_GRAY).bold();
		
		groupStyle = stl.style(boldStyle).setHorizontalAlignment(
				HorizontalAlignment.LEFT);
		subtotalStyle = stl.style(boldStyle).setTopBorder(stl.pen1Point());

		crosstabGroupStyle = stl.style(columnTitleStyle);
		crosstabGroupTotalStyle = stl.style(columnTitleStyle)
				.setBackgroundColor(new Color(170, 170, 170));
		crosstabGrandTotalStyle = stl.style(columnTitleStyle)
				.setBackgroundColor(new Color(140, 140, 140));
		crosstabCellStyle = stl.style(columnStyle).setBorder(
				stl.pen1Point());
		reportTemplate = template()
				.setLocale(TranslateManager.getInstance().getLocale()).setColumnStyle(columnStyle)
				.setColumnTitleStyle(columnTitleStyle)
				.setGroupStyle(groupStyle)
				.setGroupTitleStyle(groupStyle)
				.setSubtotalStyle(subtotalStyle)
				.highlightDetailEvenRows()
				.crosstabHighlightEvenRows()
				.setCrosstabGroupStyle(crosstabGroupStyle)
				.setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
				.setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
				.setCrosstabCellStyle(crosstabCellStyle);
		footerComponent = cmp.pageXofY()
        .setStyle(
        	stl.style(boldCenteredStyle)
        	   .setTopBorder(stl.pen1Point()));
	}
	

    private String getParameter (HttpServletRequest request, String arg, String defaultvalue) {
    	String value = request.getParameter(arg);
    	if (value == null) return defaultvalue;
    	else return value;
    }
    
    
	@Override
	protected void build(Context ct, JasperReportBuilder report) throws ReportGenerationException {
			// Add columns
			log.debug("Adding columns");
			
			for (Field f : this._fields) {
				if (f.isShown()) {
					TextColumnBuilder<String> column = col.column(f.getColumnName(), f.getID(), DataTypes.stringType());
					if (f.getFieldStyle().hasFixedSize()) column.setFixedWidth(f.getFieldStyle().getColumnsize());
					report.addColumn(column);
				}
			}
			for (Field f : this._fields) {
				if (f.isGroupChosen()) {
					TextColumnBuilder<String> groupColumn = col.column(f.getColumnName(), f.getID()+"group", DataTypes.stringType());
					ColumnGroupBuilder group = grp.group(groupColumn)
		            	.setTitleWidth(100)
		            	.setHeaderLayout(GroupHeaderLayout.TITLE_AND_VALUE)
		            	.showColumnHeaderAndFooter();
					
					report.addGroup(group);
				}
			}


			
			
			try {
				report
					.setTemplate(reportTemplate)
					.setDataSource(this.getData(ct))
					.setPageFormat(PageType.A4, PageOrientation.LANDSCAPE);
				
				log.debug("Set title");
				// Set title
				switch (_entity) {
					case AUTHOR:
						report.title(cmp.text(TranslateManager.getInstance().translate("stats.reporting.title.author", _entityID)).setStyle(bold18CenteredStyle).setHorizontalAlignment(HorizontalAlignment.CENTER));
						break;
					case COLLECTION:
						Collection c = Collection.find(ct, Integer.parseInt(_entityID));
						report.title(cmp.text(this.translate("stats.reporting.title.collection", c.getMetadata("name"))).setStyle(bold18CenteredStyle).setHorizontalAlignment(HorizontalAlignment.CENTER));
						break;
					case COMMUNITY:
						Community cm = Community.find(ct, Integer.parseInt(_entityID));
						report.title(cmp.text(this.translate("stats.reporting.title.community", cm.getMetadata("name"))).setStyle(bold18CenteredStyle).setHorizontalAlignment(HorizontalAlignment.CENTER));
						break;
					default:
						report.title(cmp.text(this.translate("stats.reporting.title.none")).setStyle(bold18CenteredStyle).setHorizontalAlignment(HorizontalAlignment.CENTER));
						break;
				}
			} catch (SQLException e) {
				throw new ReportGenerationException(e);
			}
	}
	
	/*
	private String orderClause () {
		String result = " ORDER BY ";
		String group = "";
		switch (_group) {
			case COMMCOL:
				group += "community ASC, collection ASC ";
				break;
			case COLLECTION:
				group += "collection ASC ";
				break;
			case ISSUE:
				group += "issue ASC ";
				break;
			case PEERREVIEW:
				group += "peerreview ASC ";
				break;
			case TYPE:
				group += "type ASC ";
				break;
		}
		String order = _orderfield+" "+_ordertype;
		
		if (!_orderfield.equals(_group.name().toLowerCase())) return result + group + ", " + order + " NULLS LAST ";
		else return result + order + " NULLS LAST ";
	}
	
	private List<Community> getSubAllCommunities (Community c) throws SQLException {
		if (c == null) throw new NullArgumentException("c");
		List<Community> list = new ArrayList<Community>();
		list.add(c);
		for (Community s : c.getSubcommunities()) {
			list.addAll(this.getSubAllCommunities(s));
		}
		return list;
	}
	
	private List<String> getAllSubCollections (String comID) throws SQLException {
		Community c = Community.find(new Context(), Integer.parseInt(comID));
		List<String> list = new ArrayList<String>();
		for (Community s : this.getSubAllCommunities(c)) {
			for (Collection col : s.getCollections())
				if (!list.contains(col))
					list.add(col.getID()+"");
		}
		return list;
	}
	
	private String whereClause () throws SQLException {
		String whereClause = "";
		switch (_entity) {
			case AUTHOR:
				whereClause = " WHERE i.item_id IN (SELECT tmp.item_id FROM metadatavalue tmp WHERE tmp.metadata_field_id="+ ReportDatabaseManager.getFieldID("dc", "contributor", "author")
							+" AND tmp.text_value like '"+_value+"') ";
				break;
			case COLLECTION:
				whereClause = " WHERE ( i.item_id IN (SELECT tmp.item_id FROM collection2item tmp WHERE collection_id="+_value+") OR ( i.owning_collection="+_value+"))";
				break;
			case COMMUNITY:
				whereClause = " WHERE ( i.item_id IN (SELECT tmp.item_id FROM community2item tmp WHERE community_id="+_value+")  OR (i.item_id IN ("+(String) StringUtils.join(this.getAllSubCollections(_value).toArray(), ",")+")) OR (i.owning_collection IN  ("+(String) StringUtils.join(this.getAllSubCollections(_value).toArray(), ",")+")) )";
				break;
		}
		
		if (_year != null) {
			String likeDate = _year;
			if (_month != null) likeDate += "-" + _month;
			if (!whereClause.equals("")) {
				whereClause += " AND ";
			} else whereClause += " WHERE ";
			whereClause += " i.item_id IN (SELECT tmp.item_id FROM metadatavalue tmp WHERE tmp.metadata_field_id="+ ReportDatabaseManager.getFieldID("dc", "date", "available")
									+" AND tmp.text_value like '"+likeDate+"%') ";
		}
		return whereClause;
	}
	
	

	private String query() {
		Map<String, String> values = new HashMap<String, String>();

		try {
			values.put("WhereClause", this.whereClause());
			values.put("OrderClause", this.orderClause());
			
			values.put("DefaultDateIssued", this.translate("stats.reporting.default.dateissued"));
			values.put("DefaultTitle", this.translate("stats.reporting.default.title"));
			values.put("DefaultPeerreview", this.translate("stats.reporting.default.peerreview"));
			values.put("DefaultType", this.translate("stats.reporting.default.type"));
			values.put("DefaultCollection", this.translate("stats.reporting.default.collection"));
			values.put("DefaultCommunity", this.translate("stats.reporting.default.community"));
			
			values.put("Date_Issued_ID", ReportDatabaseManager.getFieldID("dc", "date", "issued").intValue()+"");
			values.put("Title_ID", ReportDatabaseManager.getFieldID("dc", "title", null).intValue()+"");
			values.put("Peerreview_ID", ReportDatabaseManager.getFieldID("dc", "peerreview", null).intValue()+"");
			values.put("Type_ID", ReportDatabaseManager.getFieldID("dc", "type", null).intValue()+"");
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		String result = "SELECT i.item_id as item_id,"+
		"coalesce((SELECT text_value FROM metadatavalue WHERE item_id=i.item_id AND metadata_field_id={Date_Issued_ID} LIMIT 1),'{DefaultDateIssued}') as issue, "+
		"coalesce((SELECT text_value FROM metadatavalue WHERE item_id=i.item_id AND metadata_field_id={Title_ID} LIMIT 1),'{DefaultTitle}') as title, "+
		"coalesce((SELECT text_value FROM metadatavalue WHERE item_id=i.item_id AND metadata_field_id={Peerreview_ID} LIMIT 1),'{DefaultPeerreview}') as peerreview, "+
		"coalesce((SELECT text_value FROM metadatavalue WHERE item_id=i.item_id AND metadata_field_id={Type_ID} LIMIT 1),'{DefaultType}') as type, "+
		"coalesce((SELECT name FROM collection WHERE collection_id=i.owning_collection),'{DefaultCollection}') as collection, "+
		"coalesce((SELECT c.name FROM community c, community2collection cc WHERE c.community_id=cc.community_id AND cc.collection_id=i.owning_collection LIMIT 1),'{DefaultCommunity}') as community "+
		"FROM item i {WhereClause} {OrderClause}";
		for (Map.Entry<String, String> e : values.entrySet())
			result = result.replaceAll(Pattern.quote("{") + e.getKey() + Pattern.quote("}"), e.getValue());
		
		log.debug("SQL QUERY: "+result);
		
		return result;
	}*/

	protected JRDataSource getData(Context ct) throws ReportGenerationException {
		return new DSpaceItemDataSource(ct, _entity, _entityID, this._fields, _order);
	}

	@Override
	public void addFooter(JasperReportBuilder report) {
		report.pageFooter(footerComponent);
	}
	
}
