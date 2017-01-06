<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template name="chart">
		<xsl:param name="show-title"/>
		
		<div class="panel panel-info">
	

					<xsl:if test="$show-title">
						<div class="panel-heading">
							<xsl:value-of select="@title"/>
						</div>
					</xsl:if>
<div style="padding: 10px;">
          <xsl:value-of select="." disable-output-escaping="yes"/>      
</div>      
		</div>
	</xsl:template>

</xsl:stylesheet>
