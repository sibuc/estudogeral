<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:include href="resultset2table.xsl"/>
	<xsl:include href="chart.xsl"/>
	
	<xsl:template match="/">
		<div class="container">
			<div class="row">
				<div class="col-md-6">					
					<xsl:for-each select="/statistic/block[@name='files']">
						<xsl:call-template name="resultset2table">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>
						
						
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='format']">
						<xsl:call-template name="resultset2table">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>
			</div>
						
				
			<div class="row">
				<div class="col-md-12">
					<xsl:for-each select="/statistic/block[@name='format-chart']">
						<xsl:call-template name="chart">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>											
				</div>
			</div>
		</div>
		
		
	</xsl:template>
</xsl:stylesheet>

