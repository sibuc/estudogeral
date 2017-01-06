package pt.keep.dspace.chart.author;

import pt.keep.dspace.chart.AbstractChart;

public abstract class AbstractAuthorChart extends AbstractChart {
	private String _comm;
	
	public void initialize (String author) {
		_comm = author;
	}
	
	public String getAuthor () {
		return _comm;
	}
}
