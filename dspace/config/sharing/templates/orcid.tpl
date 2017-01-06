<?xml version="1.0"?>
<orcid-message xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.orcid.org/ns/orcid" xsi:schemaLocation="http://www.orcid.org/ns/orcid https://raw.github.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/orcid-message-${api_version}.xsd">
	<message-version>${api_version}</message-version>
	<orcid-profile>
		<orcid-activities>
			<orcid-works>
				<orcid-work visibility="${visibility}">
					<work-title>
						<title>${title}</title>
					</work-title>
					<short-description>${abstract}</short-description>
					<work-type>${type}</work-type>
					<work-citation>
						<work-citation-type>bibtex</work-citation-type>
						<citation>${citation}</citation>
					</work-citation>
					<publication-date>
						${publication_date}
					</publication-date>
					<work-external-identifiers>
						${identifiers}
					</work-external-identifiers>
					<url>${handle}</url>
					<work-contributors>
						${contributors}
					</work-contributors>
				</orcid-work>
			</orcid-works>
		</orcid-activities>
	</orcid-profile>
</orcid-message>
