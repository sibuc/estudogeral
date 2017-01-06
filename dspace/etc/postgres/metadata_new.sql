--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Name: metadatafieldregistry_seq; Type: SEQUENCE; Schema: public; Owner: dspace
--

CREATE SEQUENCE metadatafieldregistry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE metadatafieldregistry_seq OWNER TO dspace;

--
-- Name: metadataschemaregistry_seq; Type: SEQUENCE; Schema: public; Owner: dspace
--

CREATE SEQUENCE metadataschemaregistry_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE metadataschemaregistry_seq OWNER TO dspace;


--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: metadatafieldregistry; Type: TABLE; Schema: public; Owner: dspace; Tablespace: 
--

CREATE TABLE metadatafieldregistry (
    metadata_field_id integer DEFAULT nextval('metadatafieldregistry_seq'::regclass) NOT NULL,
    metadata_schema_id integer NOT NULL,
    element character varying(64),
    qualifier character varying(64),
    scope_note text
);


ALTER TABLE metadatafieldregistry OWNER TO dspace;

--
-- Name: metadataschemaregistry; Type: TABLE; Schema: public; Owner: dspace; Tablespace: 
--

CREATE TABLE metadataschemaregistry (
    metadata_schema_id integer DEFAULT nextval('metadataschemaregistry_seq'::regclass) NOT NULL,
    namespace character varying(256),
    short_id character varying(32)
);


ALTER TABLE metadataschemaregistry OWNER TO dspace;

--
-- Data for Name: metadatafieldregistry; Type: TABLE DATA; Schema: public; Owner: dspace
--

COPY metadatafieldregistry (metadata_field_id, metadata_schema_id, element, qualifier, scope_note) FROM stdin;
1	1	contributor	\N	A person, organization, or service responsible for the content of the resource.  Catch-all for unspecified contributors.
2	1	contributor	advisor	Use primarily for thesis advisor.
3	1	contributor	author	\N
4	1	contributor	editor	\N
5	1	contributor	illustrator	\N
6	1	contributor	other	\N
7	1	coverage	spatial	Spatial characteristics of content.
8	1	coverage	temporal	Temporal characteristics of content.
9	1	creator	\N	Do not use; only for harvested metadata.
10	1	date	\N	Use qualified form if possible.
11	1	date	accessioned	Date DSpace takes possession of item.
12	1	date	available	Date or date range item became available to the public.
13	1	date	copyright	Date of copyright.
14	1	date	created	Date of creation or manufacture of intellectual content if different from date.issued.
15	1	date	issued	Date of publication or distribution.
16	1	date	submitted	Recommend for theses/dissertations.
17	1	identifier	\N	Catch-all for unambiguous identifiers not defined by\n    qualified form; use identifier.other for a known identifier common\n    to a local collection instead of unqualified form.
18	1	identifier	citation	Human-readable, standard bibliographic citation \n    of non-DSpace format of this item
19	1	identifier	govdoc	A government document number
20	1	identifier	isbn	International Standard Book Number
21	1	identifier	issn	International Standard Serial Number
22	1	identifier	sici	Serial Item and Contribution Identifier
23	1	identifier	ismn	International Standard Music Number
24	1	identifier	other	A known identifier type common to a local collection.
25	1	identifier	uri	Uniform Resource Identifier
26	1	description	\N	Catch-all for any description not defined by qualifiers.
27	1	description	abstract	Abstract or summary.
28	1	description	provenance	The history of custody of the item since its creation, including any changes successive custodians made to it.
29	1	description	sponsorship	Information about sponsoring agencies, individuals, or\n    contractual arrangements for the item.
30	1	description	statementofresponsibility	To preserve statement of responsibility from MARC records.
31	1	description	tableofcontents	A table of contents for a given item.
32	1	description	uri	Uniform Resource Identifier pointing to description of\n    this item.
33	1	format	\N	Catch-all for any format information not defined by qualifiers.
34	1	format	extent	Size or duration.
35	1	format	medium	Physical medium.
36	1	format	mimetype	Registered MIME type identifiers.
37	1	language	\N	Catch-all for non-ISO forms of the language of the\n    item, accommodating harvested values.
38	1	language	iso	Current ISO standard for language of intellectual content, including country codes (e.g. "en_US").
39	1	publisher	\N	Entity responsible for publication, distribution, or imprint.
40	1	relation	\N	Catch-all for references to other related items.
41	1	relation	isformatof	References additional physical form.
42	1	relation	ispartof	References physically or logically containing item.
43	1	relation	ispartofseries	Series name and number within that series, if available.
44	1	relation	haspart	References physically or logically contained item.
45	1	relation	isversionof	References earlier version.
46	1	relation	hasversion	References later version.
47	1	relation	isbasedon	References source.
48	1	relation	isreferencedby	Pointed to by referenced resource.
49	1	relation	requires	Referenced resource is required to support function,\n    delivery, or coherence of item.
50	1	relation	replaces	References preceeding item.
51	1	relation	isreplacedby	References succeeding item.
52	1	relation	uri	References Uniform Resource Identifier for related item.
53	1	rights	\N	Terms governing use and reproduction.
54	1	rights	uri	References terms governing use and reproduction.
55	1	source	\N	Do not use; only for harvested metadata.
56	1	source	uri	Do not use; only for harvested metadata.
57	1	subject	\N	Uncontrolled index term.
58	1	subject	classification	Catch-all for value from local classification system;\n    global classification systems will receive specific qualifier
59	1	subject	ddc	Dewey Decimal Classification Number
60	1	subject	lcc	Library of Congress Classification Number
61	1	subject	lcsh	Library of Congress Subject Headings
62	1	subject	mesh	MEdical Subject Headings
63	1	subject	other	Local controlled vocabulary; global vocabularies will receive specific qualifier.
64	1	title	\N	Title statement/title proper.
65	1	title	alternative	Varying (or substitute) form of title proper appearing in item,\n    e.g. abbreviation or translation
66	1	type	\N	Nature or genre of content.
68	2	publication	lastPage	DeGois
69	2	publication	issue	DeGois
70	2	publication	location	DeGois
71	2	publication	title	DeGois
67	2	publication	firstPage	DeGois
72	1	date	updated	DeGois
74	1	relation	publisherversion	Versão do editor
75	1	peerreviewed	\N	Revisão
76	1	identifier	doi	Digital object identifier
77	2	publication	volume	DeGois
78	2	publication	area	Área científica - usado nas teses de doutoramento (Serviçoa Académicos)
79	2	publication	unidadeorganica	Serviços Académicos (doutoramentos)
80	1	provenance	\N	\N
81	1	rights	license	\N
82	3	abstract	\N	A summary of the resource.
83	3	accessRights	\N	Information about who can access the resource or an indication of its security status. May include information regarding access or restrictions based on privacy, security, or other policies.
84	3	accrualMethod	\N	The method by which items are added to a collection.
85	3	accrualPeriodicity	\N	The frequency with which items are added to a collection.
86	3	accrualPolicy	\N	The policy governing the addition of items to a collection.
87	3	alternative	\N	An alternative name for the resource.
88	3	audience	\N	A class of entity for whom the resource is intended or useful.
89	3	available	\N	Date (often a range) that the resource became or will become available.
90	3	bibliographicCitation	\N	Recommended practice is to include sufficient bibliographic detail to identify the resource as unambiguously as possible.
91	3	comformsTo	\N	An established standard to which the described resource conforms.
92	3	contributor	\N	An entity responsible for making contributions to the resource. Examples of a Contributor include a person, an organization, or a service.
93	3	coverage	\N	The spatial or temporal topic of the resource, the spatial applicability of the resource, or the jurisdiction under which the resource is relevant.
94	3	created	\N	Date of creation of the resource.
95	3	creator	\N	An entity primarily responsible for making the resource.
96	3	date	\N	A point or period of time associated with an event in the lifecycle of the resource.
97	3	dateAccepted	\N	Date of acceptance of the resource.
98	3	dateCopyrighted	\N	Date of copyright.
99	3	dateSubmitted	\N	Date of submission of the resource.
100	3	description	\N	An account of the resource.
101	3	educationLevel	\N	A class of entity, defined in terms of progression through an educational or training context, for which the described resource is intended.
102	3	extent	\N	The size or duration of the resource.
103	3	format	\N	The file format, physical medium, or dimensions of the resource.
104	3	hasFormat	\N	A related resource that is substantially the same as the pre-existing described resource, but in another format.
105	3	hasPart	\N	A related resource that is included either physically or logically in the described resource.
106	3	hasVersion	\N	A related resource that is a version, edition, or adaptation of the described resource.
107	3	identifier	\N	An unambiguous reference to the resource within a given context.
108	3	instructionalMethod	\N	A process, used to engender knowledge, attitudes and skills, that the described resource is designed to support.
109	3	isFormatOf	\N	A related resource that is substantially the same as the described resource, but in another format.
110	3	isPartOf	\N	A related resource in which the described resource is physically or logically included.
111	3	isReferencedBy	\N	A related resource that references, cites, or otherwise points to the described resource.
112	3	isReplacedBy	\N	A related resource that supplants, displaces, or supersedes the described resource.
113	3	isRequiredBy	\N	A related resource that requires the described resource to support its function, delivery, or coherence.
114	3	issued	\N	Date of formal issuance (e.g., publication) of the resource.
115	3	isVersionOf	\N	A related resource of which the described resource is a version, edition, or adaptation.
116	3	language	\N	A language of the resource.
117	3	license	\N	A legal document giving official permission to do something with the resource.
118	3	mediator	\N	An entity that mediates access to the resource and for whom the resource is intended or useful.
119	3	medium	\N	The material or physical carrier of the resource.
120	3	modified	\N	Date on which the resource was changed.
121	3	provenance	\N	A statement of any changes in ownership and custody of the resource since its creation that are significant for its authenticity, integrity, and interpretation.
122	3	publisher	\N	An entity responsible for making the resource available.
123	3	references	\N	A related resource that is referenced, cited, or otherwise pointed to by the described resource.
124	3	relation	\N	A related resource.
125	3	replaces	\N	A related resource that is supplanted, displaced, or superseded by the described resource.
126	3	requires	\N	A related resource that is required by the described resource to support its function, delivery, or coherence.
127	3	rights	\N	Information about rights held in and over the resource.
128	3	rightsHolder	\N	A person or organization owning or managing rights over the resource.
129	3	source	\N	A related resource from which the described resource is derived.
130	3	spatial	\N	Spatial characteristics of the resource.
131	3	subject	\N	The topic of the resource.
132	3	tableOfContents	\N	A list of subunits of the resource.
133	3	temporal	\N	Temporal characteristics of the resource.
134	3	title	\N	A name given to the resource.
135	3	type	\N	The nature or genre of the resource.
136	3	valid	\N	Date (often a range) of validity of a resource.
137	4	firstname	\N	Metadata field used for the first name
138	4	lastname	\N	Metadata field used for the last name
139	4	phone	\N	Metadata field used for the phone number
140	4	language	\N	Metadata field used for the language
141	1	description	version	The Peer Reviewed status of an item
142	1	identifier	slug	a uri supplied via the sword slug header, as a suggested uri for the item
143	1	language	rfc3066	the rfc3066 form of the language for the item
144	1	rights	holder	The owner of the copyright
146	1	date	periodoembargo	
147	1	identifier	tid	TID - Identificador do trabalho atribuído pelo RENATES
148	1	subject	fos	Área científica de acordo com a classificação da OCDE/Domínio Científico
149	5	embargofct	\N	
153	4	email	alternativo	Emails alternativos, para além do que é registado como campo obrigatório em eperson.
154	4	identifier	uc	Identificador na UC; no caso de alunos é o nº de aluno do nonio/infoestudante
155	4	preferredhandles	\N	Handles das comunidades e colecoes que pretendem ver listados ba area de utilizador
150	5	idDisciplina	uc	Dissertações de Mestrado e Provas de Doutoramento (identificação p/ ligação com nonio/infoestudante)
151	5	identifier	uc	Identificação interna do autor do trabalho na UC (redundante)
152	5	idIES	dges	Identificação da(s) instituição de ensino superior que participa no grau - mestrados e doutoramentos
156	6	degree	classification	Classificação qualitativa (teses) ou quantitativa(dissertações)
157	6	degree	discipline	Área cientifica associada ao trabalho (retirado...)
158	6	degree	disciplineID	Código (UC) da disciplina associada ao trabalho (mestrados e doutoramentos)
159	6	degree	elementojuri	Identificador do elemento do júri
160	6	degree	grantor	Nome da instituição que atribui o grau. Deve ser indicado tal como consta no documento.
161	6	degree	grantorID	Identificação da Instituição de Ensino Superior que concede o grau (código DGES)  - deverá ser suprimido
162	6	degree	grantorUnitID	Unidade Orgânica da IES
163	6	degree	level	Designação do grau obtido
164	6	degree	name	Designação do grau associado ao trabalho, tal como apresentado na obra
165	6	degree	presidentejuri	Presidente de juri de provas de mestrado/doutoramento
166	6	degree	renatesID	Código de registo do trabalho no RENATES
167	7	faculdade	\N	Designação da Unidade Orgânica
168	7	idAluno	uc	Nº de aluno na UC para ligação com o Nónio
169	7	idDisciplina	uc	
170	7	idIES	dges	
171	7	inicioprazodeposito	\N	
172	6	degree	grantorUnit	
73	1	date	embargoEndDate	Data de fim de embargo
145	1	date	embargo	Definido pelo RCAAP; idêntico a date.embargoEnd
174	8	date	periodoembargo	valor do periodo de embargo a aplicar no final da defesa das teses de doutoramento -  utilizar só para provas académicas
\.


--
-- Data for Name: metadataschemaregistry; Type: TABLE DATA; Schema: public; Owner: dspace
--

COPY metadataschemaregistry (metadata_schema_id, namespace, short_id) FROM stdin;
1	http://dublincore.org/documents/dcmi-terms/	dc
2	http://www.degois.pt/rcaap	degois
3	http://purl.org/dc/terms/	dcterms
4	http://dspace.org/eperson	eperson
5	http://rcaap.pt	rcaap
6	http://thesis.renates.pt	thesis
7	http://dspace.org/collection	collection
8	http://thesis.uc.pt	uc
\.


--
-- Name: metadatafieldregistry_pkey; Type: CONSTRAINT; Schema: public; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY metadatafieldregistry
    ADD CONSTRAINT metadatafieldregistry_pkey PRIMARY KEY (metadata_field_id);


--
-- Name: metadataschemaregistry_namespace_key; Type: CONSTRAINT; Schema: public; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY metadataschemaregistry
    ADD CONSTRAINT metadataschemaregistry_namespace_key UNIQUE (namespace);


--
-- Name: metadataschemaregistry_pkey; Type: CONSTRAINT; Schema: public; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY metadataschemaregistry
    ADD CONSTRAINT metadataschemaregistry_pkey PRIMARY KEY (metadata_schema_id);


--
-- Name: metadataschemaregistry_short_id_key; Type: CONSTRAINT; Schema: public; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY metadataschemaregistry
    ADD CONSTRAINT metadataschemaregistry_short_id_key UNIQUE (short_id);


--
-- Name: metadatafield_schema_idx; Type: INDEX; Schema: public; Owner: dspace; Tablespace: 
--

CREATE INDEX metadatafield_schema_idx ON metadatafieldregistry USING btree (metadata_schema_id);


--
-- Name: metadatafieldregistry_metadata_schema_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: dspace
--

ALTER TABLE ONLY metadatafieldregistry
    ADD CONSTRAINT metadatafieldregistry_metadata_schema_id_fkey FOREIGN KEY (metadata_schema_id) REFERENCES metadataschemaregistry(metadata_schema_id);




--
-- Name: metadatafieldregistry_seq; Type: SEQUENCE SET; Schema: public; Owner: dspace
--

SELECT pg_catalog.setval('metadatafieldregistry_seq', 174, false);


--
-- Name: metadataschemaregistry_seq; Type: SEQUENCE SET; Schema: public; Owner: dspace
--

SELECT pg_catalog.setval('metadataschemaregistry_seq', 8, true);

--
-- PostgreSQL database dump complete
--