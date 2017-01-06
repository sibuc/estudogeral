package pt.keep.dspace.report.field.database;

import pt.keep.dspace.report.exceptions.ReportConfigurationException;
import pt.keep.dspace.report.exceptions.ReportGenerationException;
import pt.keep.dspace.report.util.TranslateManager;

public class DatabaseCommunityFieldAdapter extends AbstractDatabaseFieldAdapter {

	@Override
	public void init() throws ReportConfigurationException {
		
	}

	@Override
	public String generateWhere(String tableid, String value)
			throws ReportGenerationException {
		return null;
	}

	@Override
	public String generateSelect(String tableid)
			throws ReportGenerationException {
		return "coalesce((SELECT c.name FROM community c, community2collection cc WHERE c.community_id=cc.community_id AND cc.collection_id="+tableid+".owning_collection LIMIT 1),'"+TranslateManager.getInstance().translate("stats.reporting.field."+super.getID()+".omission")+"') as "+super.getID();
	}

	@Override
	public String generateSelect(String tableid, String extraname)
			throws ReportGenerationException {
		return "coalesce((SELECT c.name FROM community c, community2collection cc WHERE c.community_id=cc.community_id AND cc.collection_id="+tableid+".owning_collection LIMIT 1),'"+TranslateManager.getInstance().translate("stats.reporting.field."+super.getID()+".omission")+"') as "+super.getID()+extraname;
	}
	
}
