package pt.keep.dspace.report.field;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.dspace.core.ConfigurationManager;
import org.dspace.core.Context;

import pt.keep.dspace.report.data.ExportEntity;
import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.field.database.AbstractDatabaseFieldAdapter;
import pt.keep.dspace.report.field.filter.AbstractFilter;
import pt.keep.dspace.report.field.group.AbstractGroupTransformer;
import pt.keep.dspace.report.field.show.FieldDefaults;
import pt.keep.dspace.report.field.show.FieldStyle;
import pt.keep.dspace.report.util.TranslateManager;

public class Field {
	private String _id;
	private boolean _groupable;
	private boolean _showable;
	private boolean _orderable;
	private boolean _filterable;
	private String _value;
	private Context _ct;
	
	public Field(Context ct, String id) {
		_id = id;
		_ct = ct;
		_groupable = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".group");
		_showable = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".show");
		_orderable = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".order");
		_filterable = ConfigurationManager.getBooleanProperty("stats.reports.field."+id+".filter");
		_entities = new ArrayList<ExportEntity>();
		String all = ConfigurationManager.getProperty("stats.reports.field."+id+".appears");
		if (all != null) {
			String[] parts = all.split(",");
			for (String s : parts) {
				if (s != null && !s.trim().equals("")) {
					_entities.add(ExportEntity.valueOf(s.trim().toUpperCase()));
				}
			}
		}
	}

	public Context getContext () {
		return _ct;
	}
	
	public boolean isGroupable() {
		return _groupable;
	}

	public boolean isShowable() {
		return _showable;
	}
	
	public boolean isFilterable () {
		return this.isGroupable() && this._filterable;
	}

	public boolean isOrderable() {
		return _orderable && this.isShowable();
	}
	
	public String getID() {
		return _id;
	}
	
	private AbstractGroupTransformer _transformer = null;
	
	public AbstractGroupTransformer getGroupTransformer () throws ReportConfigurationException {
		if (_transformer == null) {
			try {
				String classe = ConfigurationManager.getProperty("stats.reports.field."+_id+".group.class");
				if (classe == null) throw new ReportConfigurationException("Invalid property report.field."+_id+".group.class");
				Class c = Class.forName(classe);
				Object obj = c.newInstance();
				if (obj instanceof AbstractGroupTransformer) {
					((AbstractGroupTransformer) obj).initialize(_id);
					_transformer = (AbstractGroupTransformer) obj;
				}
			} catch (ClassNotFoundException e) {
				throw new ReportConfigurationException(e);
			} catch (InstantiationException e) {
				throw new ReportConfigurationException(e);
			} catch (IllegalAccessException e) {
				throw new ReportConfigurationException(e);
			} catch (ReportGenerationException e) {
				throw new ReportConfigurationException(e);
			}
		}
		return _transformer;
	}
	private FieldStyle _style = null;
	public FieldStyle getFieldStyle () {
		if (_style == null) _style = new FieldStyle(this.getID());
		return _style;
	}
	private FieldDefaults _defaults;
	public FieldDefaults getFieldDefaults () {
		if (_defaults == null) _defaults = new FieldDefaults(this.getID());
		return _defaults;
	}
	
	private AbstractDatabaseFieldAdapter _adapter = null;
	
	public AbstractDatabaseFieldAdapter getDatabaseAdapter () throws ReportConfigurationException {
		if (_adapter == null) {
			try {
				String classe = ConfigurationManager.getProperty("stats.reports.field."+_id+".database.class");
				if (classe == null) throw new ReportConfigurationException("Invalid property report.field."+_id+".database.class");
				Class c = Class.forName(classe);
				Object obj = c.newInstance();
				if (obj instanceof AbstractDatabaseFieldAdapter) {
					((AbstractDatabaseFieldAdapter) obj).initialize(_ct, _id);
					_adapter = (AbstractDatabaseFieldAdapter) obj;
				}
			} catch (ClassNotFoundException e) {
				throw new ReportConfigurationException(e);
			} catch (InstantiationException e) {
				throw new ReportConfigurationException(e);
			} catch (IllegalAccessException e) {
				throw new ReportConfigurationException(e);
			}
		}
		return _adapter;
	}
	

	private AbstractFilter _filter = null;
	
	public AbstractFilter getFilter () throws ReportConfigurationException {
		if (_filter == null) {
			try {
				String classe = ConfigurationManager.getProperty("stats.reports.field."+_id+".filter.class");
				if (classe == null) throw new ReportConfigurationException("Invalid property report.field."+_id+".filter.class");
				Class c = Class.forName(classe);
				Object obj = c.newInstance();
				if (obj instanceof AbstractFilter) {
					((AbstractFilter) obj).initialize(_ct, _id);
					_filter = (AbstractFilter) obj;
				}
			} catch (ClassNotFoundException e) {
				throw new ReportConfigurationException(e);
			} catch (InstantiationException e) {
				throw new ReportConfigurationException(e);
			} catch (IllegalAccessException e) {
				throw new ReportConfigurationException(e);
			}
		}
		return _filter;
	}

	private boolean _isgroupchosen = false;
	private boolean _isorderchosen = false;
	private boolean _isshowchosen = false;
	private boolean _isfilterchosen = false;
	private String _filterValue = null;
	
	public void fill(HttpServletRequest request) {
		_isfilterchosen = false;
		_isgroupchosen = false;
		_isorderchosen = false;
		_isshowchosen = false;
		_filterValue = null;
		
		this._value = request.getParameter(this.getID());
		if (this.isGroupable()) {
			String g =  request.getParameter("groupfield");
			if (g != null && g.equals(this.getID()))
					_isgroupchosen = true;
		}
		if (this.isOrderable()) {
			String g =  request.getParameter("orderfield");
			if (g != null && g.equals(this.getID()))
					_isorderchosen = true;
		}
		
		if (this.isShowable()) {
			if (this._value != null && this._value.equals("true"))
				_isshowchosen = true;
		}
		
		if (this.isFilterable()) {
			String h = request.getParameter(this.getID()+"filter");
			if (h != null && !h.equals("none")) {
				_isfilterchosen = true;
				_filterValue = h;
			}
		}
		
	}
	
	public boolean isFiltering () {
		return _isfilterchosen;
	}
	
	public String getFilterValue () {
		return _filterValue;
	}
	
	public boolean isShown () {
		return _isshowchosen;
	}

	public boolean isOrderChosen () {
		return _isorderchosen;
	}
	
	public boolean isGroupChosen () {
		return _isgroupchosen;
	}
	
	public String getColumnName () throws ReportGenerationException {
		return TranslateManager.getInstance().translate("stats.reporting.field."+this.getID()+".column.name");
	}
	
	public String getValue () {
		return _value;
	}
	
	private List<ExportEntity> _entities = null;
	
	public boolean appears (ExportEntity entity) {
		if (_entities == null) return false;
		else return _entities.contains(entity);
	}
}
