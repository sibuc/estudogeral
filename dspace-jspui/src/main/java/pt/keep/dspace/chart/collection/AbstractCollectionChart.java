package pt.keep.dspace.chart.collection;


import javax.servlet.jsp.PageContext;

import org.dspace.content.Collection;
import pt.keep.dspace.chart.AbstractChart;

public abstract class AbstractCollectionChart extends AbstractChart{

	public abstract String generateGraph (Collection c, PageContext page);
	public abstract String generateContainer (Collection c, PageContext page);
}
