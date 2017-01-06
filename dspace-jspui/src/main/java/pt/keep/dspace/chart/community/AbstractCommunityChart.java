package pt.keep.dspace.chart.community;

import javax.servlet.jsp.PageContext;

import org.dspace.content.Community;

import pt.keep.dspace.chart.AbstractChart;

public abstract class AbstractCommunityChart extends AbstractChart{
	public abstract String generateGraph (Community c, PageContext page);
	public abstract String generateContainer (Community c, PageContext page);
}
