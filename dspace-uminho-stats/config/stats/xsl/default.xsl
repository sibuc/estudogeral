<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:include href="resultset2table.xsl"/>
	<xsl:include href="chart.xsl"/>
	
	<xsl:template match="/">
	
		<div class="container">
			<xsl:for-each select="/statistic/block">
				<div class="row">
					<div class="col-md-12">
						<xsl:choose>
							<xsl:when test="@type = 'query'">
								<xsl:call-template name="resultset2table">
									<xsl:with-param name="show-title" select="true()"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="@type = 'chart'">
								<xsl:call-template name="chart">
									<xsl:with-param name="show-title" select="true()"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="@type = 'html'">
								<xsl:value-of select="." disable-output-escaping="yes"/>
							</xsl:when>
						</xsl:choose>
					</div>
				</div>
			</xsl:for-each>
		</div>
		
	</xsl:template>
</xsl:stylesheet>
