<?xml version="1.0" encoding="utf-8"?>
<!-- dc-ojs-ingest.xsl
 -->

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:dim="http://www.dspace.org/xmlns/dspace/dim"
        xmlns:dc="http://purl.org/dc/elements/1.1/"
        xmlns:xml="http://www.w3.org/XML/1998/namespace"
        xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
        version="1.0">

<!-- This stylesheet converts incoming DC metadata from OJS into DSpace Interal Metadata format (DIM) -->

	<!-- Catch all.  This template will ensure that nothing
	     other than explicitly what we want to xwalk will be dealt
	     with -->
	<xsl:template match="text()"></xsl:template>

    <!-- match the top level descriptionSet element and kick off the
         template matching process -->
    <!--xsl:template match="/">
    	<dim:list>
    		<xsl:apply-templates/>
    	</dim:list>
    </xsl:template-->

    
    <!-- match the records element and kick off the
         template matching process -->
    <xsl:template match="/oai_dc:dc">
    	<dim:dim dspaceType="ITEM">
    		<xsl:apply-templates/>
    	</dim:dim>
    </xsl:template>
    

    <!-- general matcher for all "statement" elements -->
    <xsl:template match="dc:creator" priority="10">
    	<!-- creator element: dc.contributor.author -->
		<dim:field mdschema="dc" element="contributor" qualifier="author">
			<xsl:value-of select="text()"/>
		</dim:field>
    </xsl:template>

    <xsl:template match="dc:identifier" priority="10">
    	<!-- creator element: dc.identifier.uri -->
		<dim:field mdschema="dc" element="identifier" qualifier="uri">
			<xsl:value-of select="text()"/>
		</dim:field>
    </xsl:template>
    
    <!-- Apply to all.  This template will ensure all other elements 
         will be processed using this method -->
    <xsl:template match="oai_dc:dc/*" priority="1">
        <xsl:if test="text() != ''">
            <xsl:element name="dim:field">
                    <xsl:attribute name="mdschema">dc</xsl:attribute>
                    <xsl:attribute name="element">
                        <xsl:value-of select="substring-after(name(),':')"/>
                    </xsl:attribute>
                    <xsl:if test="@xml:lang">
                        <xsl:attribute name="lang">
                            <xsl:value-of select="@xml:lang"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="normalize-space(.)"/>
            </xsl:element>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
