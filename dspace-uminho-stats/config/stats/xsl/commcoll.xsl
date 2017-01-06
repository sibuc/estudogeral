<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:include href="singleresultset2form.xsl"/>
	<xsl:include href="resultset2table.xsl"/>
	<xsl:include href="chart.xsl"/>
	
	<xsl:template match="/">
		<div class="container">
			
			<!--Totals-->
			<div class="row">				
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='totals']">
						<xsl:call-template name="singleresultset2form">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>
				
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='averages']">
						<xsl:call-template name="singleresultset2form">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>						
			</div>
			
			<!--Lista-->
 			<div class="row">				
				<div class="col-md-12">
					<xsl:for-each select="/statistic/block[@name='lista']">
						<xsl:call-template name="resultset2table">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>					
			</div>

 		
			<!--Country-->
<!--			<div class="row">				
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='country']">
						<xsl:call-template name="resultset2table">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>
				
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='country-chart']">
						<xsl:call-template name="chart">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>					
			</div>-->
			
			<!--Views-->
<!--			<div class="row">				
				<div class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='views-country']">
						<xsl:call-template name="resultset2table">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>
				
				<dic class="col-md-6">
					<xsl:for-each select="/statistic/block[@name='views-country-chart']">
						<xsl:call-template name="chart">
							<xsl:with-param name="show-title" select="true()"/>
						</xsl:call-template>
					</xsl:for-each>							
				</div>					
			</div>-->						
		</div>
		
	</xsl:template>
</xsl:stylesheet>

