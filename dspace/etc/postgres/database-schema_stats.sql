SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE SCHEMA stats;

ALTER SCHEMA stats OWNER TO dspace;

SET search_path = stats, pg_catalog;



--
-- Name: item_access(integer); Type: FUNCTION; Schema: stats; Owner: dspace
--

CREATE FUNCTION item_access(integer) RETURNS text
    LANGUAGE plpgsql
    AS $_$
DECLARE
   nitem alias for $1;
   temp_i int4;
BEGIN

      temp_i := 0;
      select count(*)
      into temp_i
      from resourcepolicy r, item2bundle i
      where r.resource_id = i.bundle_id
      and resource_type_id=1 and epersongroup_id = 33
      and item_id = nitem;

      if (temp_i=0) then
         return 'open access';
      else
         return 'restrict access';
      end if;

END;
$_$;


ALTER FUNCTION stats.item_access(integer) OWNER TO dspace;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: agent; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE agent (
    agent_id integer NOT NULL,
    name character varying(512) NOT NULL,
    count integer
);


ALTER TABLE stats.agent OWNER TO dspace;

--
-- Name: agent_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE agent_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.agent_seq OWNER TO dspace;

--
-- Name: agent_staging; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE agent_staging (
    agent_id integer NOT NULL,
    name character varying(512) NOT NULL,
    ip character varying(64) NOT NULL,
    type integer NOT NULL,
    spider boolean DEFAULT false,
    keepwatched boolean DEFAULT false
);


ALTER TABLE stats.agent_staging OWNER TO dspace;

--
-- Name: agent_staging_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE agent_staging_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.agent_staging_seq OWNER TO dspace;

--
-- Name: author_item; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE author_item (
    author text,
    item_id integer
);


ALTER TABLE stats.author_item OWNER TO dspace;

--
-- Name: control; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE control (
    control_id integer NOT NULL,
    last_line_log character varying(255),
    closed date,
    agg_start date,
    agg_end date
);


ALTER TABLE stats.control OWNER TO dspace;

--
-- Name: country; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE country (
    code character varying(5) NOT NULL,
    name character varying(100)
);


ALTER TABLE stats.country OWNER TO dspace;

--
-- Name: download; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download (
    download_id integer NOT NULL,
    date date,
    "time" time without time zone,
    bitstream_id integer,
    item_id integer,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64),
    country_code character varying(5),
    relative_value double precision
);


ALTER TABLE stats.download OWNER TO dspace;

--
-- Name: download_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_coll_month (
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_coll_month OWNER TO dspace;

--
-- Name: download_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_comm_month (
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_comm_month OWNER TO dspace;

--
-- Name: download_count; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_count (
    item_id integer,
    count integer
);


ALTER TABLE stats.download_count OWNER TO dspace;

--
-- Name: download_country_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_country_coll_month (
    country_code character varying(5) NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_country_coll_month OWNER TO dspace;

--
-- Name: download_country_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_country_comm_month (
    country_code character varying(5) NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_country_comm_month OWNER TO dspace;

--
-- Name: download_country_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_country_month (
    country_code character varying(5) NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_country_month OWNER TO dspace;

--
-- Name: download_item_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_item_coll_month (
    item_id integer NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_item_coll_month OWNER TO dspace;

--
-- Name: download_item_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_item_comm_month (
    item_id integer NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_item_comm_month OWNER TO dspace;

--
-- Name: download_item_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_item_month (
    item_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_item_month OWNER TO dspace;

--
-- Name: download_metadata_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_metadata_coll_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_metadata_coll_month OWNER TO dspace;

--
-- Name: download_metadata_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_metadata_comm_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_metadata_comm_month OWNER TO dspace;

--
-- Name: download_metadata_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_metadata_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_metadata_month OWNER TO dspace;

--
-- Name: download_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_month (
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL,
    relative_value double precision NOT NULL
);


ALTER TABLE stats.download_month OWNER TO dspace;

--
-- Name: download_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE download_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.download_seq OWNER TO dspace;

--
-- Name: download_spider; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE download_spider (
    download_id integer NOT NULL,
    date date,
    "time" time without time zone,
    bitstream_id integer,
    item_id integer,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64),
    country_code character varying(5),
    relative_value double precision
);


ALTER TABLE stats.download_spider OWNER TO dspace;


--
-- Name: ip_institution; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE ip_institution (
    ip_range character varying(20) NOT NULL
);


ALTER TABLE stats.ip_institution OWNER TO dspace;

--
-- Name: ip_keepwatched; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE ip_keepwatched (
    ip character varying(64) NOT NULL,
    robotstxt integer DEFAULT 0 NOT NULL,
    robottraps integer DEFAULT 0 NOT NULL,
    manuals integer DEFAULT 0 NOT NULL,
    last_agent character varying(512)
);


ALTER TABLE stats.ip_keepwatched OWNER TO dspace;

--
-- Name: ip_spider; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE ip_spider (
    ip character varying(64) NOT NULL,
    agent_id integer NOT NULL,
    country_code character varying(5) NOT NULL
);


ALTER TABLE stats.ip_spider OWNER TO dspace;

--
-- Name: language; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE language (
    id character varying(5) NOT NULL,
    name character varying(75)
);


ALTER TABLE stats.language OWNER TO dspace;

--
-- Name: login; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE login (
    login_id integer NOT NULL,
    date date,
    "time" time without time zone,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64),
    country_code character varying(5)
);


ALTER TABLE stats.login OWNER TO dspace;

--
-- Name: login_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE login_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.login_seq OWNER TO dspace;

--
-- Name: metadata_aggreg; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE metadata_aggreg (
    metadata_field_id integer NOT NULL
);


ALTER TABLE stats.metadata_aggreg OWNER TO dspace;

--
-- Name: month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE month (
    month character varying(2) NOT NULL
);


ALTER TABLE stats.month OWNER TO dspace;

--
--
-- Name: temp; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE temp (
    name character varying(512)
);


ALTER TABLE stats.temp OWNER TO dspace;


create view v_communities2item as
    with recursive
    communities as (
	select 		cc.community_id, ci.item_id
	from 		public.collection2item ci
	inner join	public.community2collection cc on cc.collection_id = ci.collection_id 
        union all
	SELECT 	t.parent_comm_id as community_id, rt.item_id
	FROM 	public.community2community t
	inner join 	communities rt ON rt.community_id = t.child_comm_id        
    )
    select * from communities;

--
-- Name: v_down_date_ip_bits_average; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_down_date_ip_bits_average AS
    SELECT a.date AS data, a.ip, a.bitstream_id, a.minimo, a.maximo, a.contador, a.relative, date_trunc('second'::text, ((a.maximo - a.minimo) / (a.contador)::double precision)) AS media FROM (SELECT download.date, download.bitstream_id, download.ip, min(download."time") AS minimo, max(download."time") AS maximo, count(*) AS contador, sum(download.relative_value) AS relative FROM download, control WHERE ((download.bitstream_id IS NOT NULL) AND (download.date > control.closed)) GROUP BY download.date, download.bitstream_id, download.ip HAVING (count(*) > 1)) a WHERE ((((a.maximo - a.minimo) >= ('01:00:00'::time without time zone)::interval) AND (((a.maximo - a.minimo) / (a.contador)::double precision) < ('00:10:00'::time without time zone)::interval)) OR (((a.maximo - a.minimo) < ('01:00:00'::time without time zone)::interval) AND (((a.maximo - a.minimo) / (a.contador)::double precision) < ('00:05:00'::time without time zone)::interval)));


ALTER TABLE stats.v_down_date_ip_bits_average OWNER TO dspace;

--
-- Name: v_down_date_ip_bits_minute; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_down_date_ip_bits_minute AS
    SELECT download.date AS data, date_trunc('minute'::text, (download."time")::interval) AS minuto, download.bitstream_id, download.ip, count(*) AS numero, sum(download.relative_value) AS relative FROM download, control WHERE ((download.bitstream_id IS NOT NULL) AND (download.date > control.closed)) GROUP BY download.date, date_trunc('minute'::text, (download."time")::interval), download.bitstream_id, download.ip HAVING (count(*) > 1);


ALTER TABLE stats.v_down_date_ip_bits_minute OWNER TO dspace;

--
-- Name: v_down_date_ip_bits_time; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_down_date_ip_bits_time AS
    SELECT download.date AS data, download."time" AS tempo, download.bitstream_id, download.ip, count(*) AS numero FROM download, control WHERE ((download.bitstream_id IS NOT NULL) AND (download.date > control.closed)) GROUP BY download.date, download."time", download.bitstream_id, download.ip HAVING (count(*) > 1);


ALTER TABLE stats.v_down_date_ip_bits_time OWNER TO dspace;

--
-- Name: v_download; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_download AS
    SELECT v.download_id, v.date, v."time", v.bitstream_id, v.item_id, h.handle, v.session_id, v.user_id, v.ip, v.country_code, v.relative_value FROM download v, public.handle h WHERE ((v.item_id = h.resource_id) AND (h.resource_type_id = 2));


ALTER TABLE stats.v_download OWNER TO dspace;

--
-- Name: v_item; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item AS
    SELECT item.item_id, item.submitter_id, item.in_archive, item.withdrawn, item.owning_collection, item.last_modified FROM public.item WHERE ((item.in_archive = true) AND (item.withdrawn = false));


ALTER TABLE stats.v_item OWNER TO dspace;

--
-- Name: v_itemsbyauthor; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_itemsbyauthor AS
    SELECT i.item_id, m.text_value AS author FROM v_item i, public.metadatavalue m WHERE ((i.item_id = m.resource_id) AND (m.metadata_field_id = 3));


ALTER TABLE stats.v_itemsbyauthor OWNER TO dspace;

--
-- Name: v_download_author; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_download_author AS
    SELECT d.download_id, d.bitstream_id, d.item_id, d.session_id, d.user_id, d.date, d."time", d.ip, d.country_code, d.relative_value, ia.author FROM download d, v_itemsbyauthor ia WHERE (d.item_id = ia.item_id);


ALTER TABLE stats.v_download_author OWNER TO dspace;

--
-- Name: v_download_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_download_coll AS
    SELECT d.download_id, d.bitstream_id, d.item_id, d.session_id, d.user_id, d.date, d."time", d.ip, d.country_code, d.relative_value, ci.collection_id FROM download d, public.collection2item ci WHERE (d.item_id = ci.item_id);


ALTER TABLE stats.v_download_coll OWNER TO dspace;

--
-- Name: v_download_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_download_comm AS
    SELECT d.download_id, d.bitstream_id, d.item_id, d.session_id, d.user_id, d.date, d."time", d.ip, d.country_code, d.relative_value, ci.community_id FROM download d, v_communities2item ci WHERE (d.item_id = ci.item_id);


ALTER TABLE stats.v_download_comm OWNER TO dspace;

--
-- Name: v_files; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_files AS
    SELECT b.bitstream_format_id, b.size_bytes AS size, 
           bf.mimetype, bf.short_description, ib.item_id 
    FROM public.bitstream b, public.bitstreamformatregistry bf, 
         public.bundle2bitstream bb, public.bundle bu, public.item2bundle ib, public.item i 
   WHERE ((((((((b.deleted = false) AND (b.bitstream_format_id = bf.bitstream_format_id)) 
           AND (b.bitstream_id = bb.bitstream_id)) 
          AND (bb.bundle_id = bu.bundle_id)) 
         AND (bu.bundle_id = ib.bundle_id)) 
        AND (ib.item_id = i.item_id)) 
       AND (i.in_archive = true)) 
      AND (i.withdrawn = false));


ALTER TABLE stats.v_files OWNER TO dspace;

--
-- Name: v_files_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_files_coll AS
    SELECT v.bitstream_format_id, v.size, v.mimetype, v.short_description, v.item_id, c.collection_id FROM v_files v, public.collection2item c WHERE (v.item_id = c.item_id);


ALTER TABLE stats.v_files_coll OWNER TO dspace;

--
-- Name: v_files_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_files_comm AS
    SELECT v.bitstream_format_id, v.size, v.mimetype, v.short_description, v.item_id, c.community_id FROM v_files v, v_communities2item c WHERE (v.item_id = c.item_id);


ALTER TABLE stats.v_files_comm OWNER TO dspace;

--
-- Name: v_item2bitstream; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item2bitstream AS
    SELECT i.item_id, bi.bitstream_id FROM public.item i, public.item2bundle ib, 
           public.bundle bu, public.bundle2bitstream bb, public.bitstream bi 
    WHERE ((((i.item_id = ib.item_id) AND (ib.bundle_id = bu.bundle_id)) 
        AND (bu.bundle_id = bb.bundle_id)) 
       AND (bb.bitstream_id = bi.bitstream_id));


ALTER TABLE stats.v_item2bitstream OWNER TO dspace;

--
-- Name: v_item_access; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_access AS
    SELECT i.item_id, btrim(dc.text_value) AS access FROM public.item i, public.dcvalue dc WHERE ((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 53));


ALTER TABLE stats.v_item_access OWNER TO dspace;

--
-- Name: v_item_access_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_access_coll AS
    SELECT i.item_id, btrim(dc.text_value) AS access, ci.collection_id FROM public.item i, public.dcvalue dc, public.collection2item ci WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 53)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_access_coll OWNER TO dspace;

--
-- Name: v_item_access_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_access_comm AS
    SELECT i.item_id, btrim(dc.text_value) AS access, ci.community_id FROM public.item i, public.dcvalue dc, v_communities2item ci WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 53)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_access_comm OWNER TO dspace;

--
-- Name: v_item_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_coll AS
    SELECT i.item_id, i.submitter_id, i.in_archive, i.withdrawn, i.owning_collection, i.last_modified, ci.collection_id FROM public.item i, public.collection2item ci WHERE (((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_coll OWNER TO dspace;

--
-- Name: v_item_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_comm AS
    SELECT i.item_id, i.submitter_id, i.in_archive, i.withdrawn, i.owning_collection, i.last_modified, ci.community_id FROM public.item i, v_communities2item ci WHERE (((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_comm OWNER TO dspace;

--
-- Name: v_item_commit; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_commit AS
    SELECT i.item_id, to_date(substr(dc.text_value, 29, 10), 'YYYY-MM-DD'::text) AS data FROM public.item i, public.dcvalue dc WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 28)) AND (substr(dc.text_value, 1, 4) = 'Made'::text));


ALTER TABLE stats.v_item_commit OWNER TO dspace;

--
-- Name: v_item_commit_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_commit_coll AS
    SELECT i.item_id, to_date(substr(dc.text_value, 29, 10), 'YYYY-MM-DD'::text) AS data, ci.collection_id FROM public.item i, public.dcvalue dc, public.collection2item ci WHERE ((((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 28)) AND (substr(dc.text_value, 1, 4) = 'Made'::text)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_commit_coll OWNER TO dspace;

--
-- Name: v_item_commit_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_commit_comm AS
    SELECT i.item_id, to_date(substr(dc.text_value, 29, 10), 'YYYY-MM-DD'::text) AS data, ci.community_id FROM public.item i, public.dcvalue dc, v_communities2item ci WHERE ((((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 28)) AND (substr(dc.text_value, 1, 4) = 'Made'::text)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_commit_comm OWNER TO dspace;

--
-- Name: v_item_language; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_language AS
    SELECT i.item_id, l.name AS language FROM public.item i, public.dcvalue dc, language l WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 38)) AND (dc.text_value = (l.id)::text));


ALTER TABLE stats.v_item_language OWNER TO dspace;

--
-- Name: v_item_language_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_language_coll AS
    SELECT i.item_id, l.name AS language, ci.collection_id FROM public.item i, public.dcvalue dc, public.collection2item ci, language l WHERE ((((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 38)) AND (i.item_id = ci.item_id)) AND (dc.text_value = (l.id)::text));


ALTER TABLE stats.v_item_language_coll OWNER TO dspace;

--
-- Name: v_item_language_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_language_comm AS
    SELECT i.item_id, l.name AS language, ci.community_id FROM public.item i, public.dcvalue dc, v_communities2item ci, language l WHERE ((((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 38)) AND (i.item_id = ci.item_id)) AND (dc.text_value = (l.id)::text));


ALTER TABLE stats.v_item_language_comm OWNER TO dspace;

--
-- Name: v_item_type; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_type AS
    SELECT i.item_id, dc.text_value AS type FROM public.item i, public.dcvalue dc WHERE ((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 66));


ALTER TABLE stats.v_item_type OWNER TO dspace;

--
-- Name: v_item_type_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_type_coll AS
    SELECT i.item_id, dc.text_value AS type, ci.collection_id FROM public.item i, public.dcvalue dc, public.collection2item ci WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 66)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_type_coll OWNER TO dspace;

--
-- Name: v_item_type_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_item_type_comm AS
    SELECT i.item_id, dc.text_value AS type, ci.community_id FROM public.item i, public.dcvalue dc, v_communities2item ci WHERE (((((i.in_archive = true) AND (i.withdrawn = false)) AND (i.item_id = dc.resource_id)) AND (dc.dc_type_id = 66)) AND (i.item_id = ci.item_id));


ALTER TABLE stats.v_item_type_comm OWNER TO dspace;

--
-- Name: v_itemsbydate; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_itemsbydate AS
    SELECT i.item_id, m.text_value AS date_issued FROM v_item i, public.metadatavalue m WHERE ((i.item_id = m.resource_id) AND (m.metadata_field_id = 15));


ALTER TABLE stats.v_itemsbydate OWNER TO dspace;

--
-- Name: v_itemsbydateavailable; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_itemsbydateavailable AS
    SELECT i.item_id, m.text_value AS date_available FROM v_item i, public.metadatavalue m WHERE ((i.item_id = m.resource_id) AND (m.metadata_field_id = 12));


ALTER TABLE stats.v_itemsbydateavailable OWNER TO dspace;

--
-- Name: view; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view (
    view_id integer NOT NULL,
    date date,
    "time" time without time zone,
    item_id integer,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64),
    country_code character varying(5)
);


ALTER TABLE stats.view OWNER TO dspace;

--
-- Name: v_session_analysis; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_session_analysis AS
    SELECT c.ip, c.country_code, c.date, (c.hour)::time without time zone AS hour, c.first, c.last, ((c.last - c.first))::time without time zone AS "time", c.hits, c.dhits, (((c.last - c.first) / (c.hits)::double precision))::time without time zone AS hitcadency, c.sessions FROM (SELECT hits.date, date_trunc('hour'::text, (hits."time")::interval) AS hour, hits.ip, hits.country_code, min(hits."time") AS first, max(hits."time") AS last, count(*) AS hits, count(DISTINCT hits.object_id) AS dhits, count(DISTINCT hits.session_id) AS sessions FROM (SELECT view.date, view."time", view.ip, view.country_code, view.session_id, view.item_id AS object_id FROM view, control WHERE (view.date > control.closed) UNION ALL SELECT download.date, download."time", download.ip, download.country_code, download.session_id, download.bitstream_id AS object_id FROM download, control WHERE (download.date > control.closed)) hits GROUP BY hits.date, date_trunc('hour'::text, (hits."time")::interval), hits.ip, hits.country_code HAVING (count(*) > 1)) c WHERE (((((c.last - c.first) / (c.hits)::double precision) < ('00:01:00'::time without time zone)::interval) AND ((((c.sessions)::double precision / (c.hits)::real) * (100)::double precision) > (95)::double precision)) AND (c.hits > 10)) ORDER BY c.ip, c.date, (c.hour)::time without time zone;


ALTER TABLE stats.v_session_analysis OWNER TO dspace;

--
-- Name: v_session_keepwatched_analysis; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_session_keepwatched_analysis AS
    SELECT c.ip, c.country_code, c.date, (c.hour)::time without time zone AS hour, c.first, c.last, ((c.last - c.first))::time without time zone AS "time", c.hits, c.dhits, (((c.last - c.first) / (c.hits)::double precision))::time without time zone AS hitcadency, c.sessions, (((c.sessions)::double precision / (c.hits)::real) * (100)::double precision) AS sessionsinhits FROM (SELECT hits.date, date_trunc('hour'::text, (hits."time")::interval) AS hour, hits.ip, hits.country_code, min(hits."time") AS first, max(hits."time") AS last, count(*) AS hits, count(DISTINCT hits.object_id) AS dhits, count(DISTINCT hits.session_id) AS sessions FROM (SELECT v.date, v."time", v.ip, v.country_code, v.session_id, v.item_id AS object_id FROM view v, control c, ip_keepwatched k WHERE ((v.date > c.closed) AND ((v.ip)::text = (k.ip)::text)) UNION ALL SELECT d.date, d."time", d.ip, d.country_code, d.session_id, d.bitstream_id AS object_id FROM download d, control c, ip_keepwatched k WHERE ((d.date > c.closed) AND ((d.ip)::text = (k.ip)::text))) hits GROUP BY hits.date, date_trunc('hour'::text, (hits."time")::interval), hits.ip, hits.country_code HAVING (count(*) > 1)) c ORDER BY c.ip, c.date, (c.hour)::time without time zone;


ALTER TABLE stats.v_session_keepwatched_analysis OWNER TO dspace;

--
-- Name: v_view; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_view AS
    SELECT v.view_id, v.date, v."time", v.item_id, h.handle, v.session_id, v.user_id, v.ip, v.country_code FROM view v, public.handle h WHERE ((v.item_id = h.resource_id) AND (h.resource_type_id = 2));


ALTER TABLE stats.v_view OWNER TO dspace;

--
-- Name: v_view_author; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_view_author AS
    SELECT v.view_id, v.handle, v.item_id, v.session_id, v.user_id, v.date, v."time", v.ip, v.country_code, ia.author FROM v_view v, v_itemsbyauthor ia WHERE (v.item_id = ia.item_id);


ALTER TABLE stats.v_view_author OWNER TO dspace;

--
-- Name: v_view_coll; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_view_coll AS
    SELECT v.view_id, v.handle, v.item_id, v.session_id, v.user_id, v.date, v."time", v.ip, v.country_code, ci.collection_id FROM v_view v, public.collection2item ci WHERE (v.item_id = ci.item_id);


ALTER TABLE stats.v_view_coll OWNER TO dspace;

--
-- Name: v_view_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_view_comm AS
    SELECT v.view_id, v.handle, v.item_id, v.session_id, v.user_id, v.date, v."time", v.ip, v.country_code, ci.community_id FROM v_view v, v_communities2item ci WHERE (v.item_id = ci.item_id);


ALTER TABLE stats.v_view_comm OWNER TO dspace;

--
-- Name: v_view_date_ip_item_time; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_view_date_ip_item_time AS
    SELECT view.date AS data, view."time" AS tempo, view.item_id, view.ip, count(*) AS numero FROM view, control WHERE ((view.item_id IS NOT NULL) AND (view.date > control.closed)) GROUP BY view.date, view."time", view.item_id, view.ip HAVING (count(*) > 1);


ALTER TABLE stats.v_view_date_ip_item_time OWNER TO dspace;

--
-- Name: workflow_steps; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE workflow_steps (
    code integer NOT NULL,
    name character varying(64) NOT NULL,
    taken boolean NOT NULL
);


ALTER TABLE stats.workflow_steps OWNER TO dspace;

--
-- Name: v_workflow; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_workflow AS
    SELECT ws.name AS state_desc, w.state, '' AS owner, w.collection_id, c.name AS collection_name, 
  dc.text_value AS title, cc.community_id FROM public.workflowitem w, public.collection c, 
  public.community2collection cc, public.community co, public.dcvalue dc, workflow_steps ws 
WHERE (((((((w.owner IS NULL) AND (w.collection_id = c.collection_id)) AND 
      (c.collection_id = cc.collection_id)) AND 
      (cc.community_id = co.community_id)) AND 
      (w.item_id = dc.resource_id)) AND 
      (dc.dc_type_id = 64)) AND 
      (w.state = ws.code)) 
UNION ALL SELECT ws.name AS state_desc, w.state, ep.email AS owner, w.collection_id, 
      dc.text_value AS title, cc.community_id FROM public.workflowitem w, public.eperson ep, 
      public.collection c, public.community2collection cc, public.community co, public.dcvalue dc, 
      workflow_steps ws 
WHERE ((((((((w.owner IS NOT NULL) AND (w.owner = ep.eperson_id)) 
        AND (w.collection_id = c.collection_id)) 
       AND (c.collection_id = cc.collection_id)) 
      AND (cc.community_id = co.community_id)) 
     AND (w.item_id = dc.resource_id)) 
    AND (dc.dc_type_id = 64)) 
   AND (w.state = ws.code));


ALTER TABLE stats.v_workflow OWNER TO dspace;

--
-- Name: workflow; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE workflow (
    workflow_id integer NOT NULL,
    date date,
    "time" time without time zone,
    workflow_item_id integer,
    item_id integer,
    collection_id integer,
    old_state integer,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64)
);


ALTER TABLE stats.workflow OWNER TO dspace;

--
-- Name: v_workflow_comm; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_workflow_comm AS
    SELECT w.workflow_id, w.workflow_item_id, w.item_id, w.collection_id, w.old_state, w.session_id, w.user_id, w.date, w."time", w.ip, cc.community_id FROM workflow w, public.community2collection cc WHERE (w.collection_id = cc.collection_id);


ALTER TABLE stats.v_workflow_comm OWNER TO dspace;

--
-- Name: v_ycct; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW v_ycct AS
    SELECT substr(p.date_issued, 1, 4) AS year, cc.community_id, ci.collection_id, t.type, count(*) AS number FROM v_itemsbydate p, public.collection2item ci, public.community2collection cc, v_item_type t WHERE (((p.item_id = ci.item_id) AND (ci.collection_id = cc.collection_id)) AND (p.item_id = t.item_id)) GROUP BY substr(p.date_issued, 1, 4), cc.community_id, ci.collection_id, t.type ORDER BY substr(p.date_issued, 1, 4), cc.community_id, ci.collection_id, t.type;


ALTER TABLE stats.v_ycct OWNER TO dspace;

--
-- Name: view_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_coll_month (
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_coll_month OWNER TO dspace;

--
-- Name: view_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_comm_month (
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_comm_month OWNER TO dspace;

--
-- Name: view_count; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_count (
    item_id integer,
    count integer
);


ALTER TABLE stats.view_count OWNER TO dspace;

--
-- Name: view_country_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_country_coll_month (
    country_code character varying(5) NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_country_coll_month OWNER TO dspace;

--
-- Name: view_country_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_country_comm_month (
    country_code character varying(5) NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_country_comm_month OWNER TO dspace;

--
-- Name: view_country_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_country_month (
    country_code character varying(5) NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_country_month OWNER TO dspace;

--
-- Name: view_item_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_item_coll_month (
    item_id integer NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_item_coll_month OWNER TO dspace;

--
-- Name: view_item_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_item_comm_month (
    item_id integer NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_item_comm_month OWNER TO dspace;

--
-- Name: view_item_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_item_month (
    item_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_item_month OWNER TO dspace;

--
-- Name: view_metadata_coll_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_metadata_coll_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    collection_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_metadata_coll_month OWNER TO dspace;

--
-- Name: view_metadata_comm_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_metadata_comm_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    community_id integer NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_metadata_comm_month OWNER TO dspace;

--
-- Name: view_metadata_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_metadata_month (
    field_id integer NOT NULL,
    field_value character varying(200) NOT NULL,
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_metadata_month OWNER TO dspace;

--
-- Name: view_month; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_month (
    yearmonth integer NOT NULL,
    year integer NOT NULL,
    value bigint NOT NULL
);


ALTER TABLE stats.view_month OWNER TO dspace;

--
-- Name: view_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE view_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.view_seq OWNER TO dspace;

--
-- Name: view_spider; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE view_spider (
    view_id integer NOT NULL,
    date date,
    "time" time without time zone,
    item_id integer,
    session_id character varying(64),
    user_id character varying(64),
    ip character varying(64),
    country_code character varying(5)
);


ALTER TABLE stats.view_spider OWNER TO dspace;

--
-- Name: workflow_intervals; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE workflow_intervals (
    item_id integer NOT NULL,
    "interval" bigint
);


ALTER TABLE stats.workflow_intervals OWNER TO dspace;

--
-- Name: workflow_seq; Type: SEQUENCE; Schema: stats; Owner: dspace
--

CREATE SEQUENCE workflow_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stats.workflow_seq OWNER TO dspace;

--
-- Name: year; Type: TABLE; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE TABLE year (
    year integer NOT NULL
);


ALTER TABLE stats.year OWNER TO dspace;

--
-- Name: z_download_unagg_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_coll_month AS
    SELECT ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_coll_month OWNER TO dspace;

--
-- Name: z_download_unagg_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_comm_month AS
    SELECT ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_comm_month OWNER TO dspace;

--
-- Name: z_download_unagg_country_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_country_coll_month AS
    SELECT ci.collection_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_country_coll_month OWNER TO dspace;

--
-- Name: z_download_unagg_country_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_country_comm_month AS
    SELECT ci.community_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_country_comm_month OWNER TO dspace;

--
-- Name: z_download_unagg_country_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_country_month AS
    SELECT download.country_code, date_trunc('month'::text, (download.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(download.relative_value) AS relative_value FROM download, control WHERE ((download.date >= control.agg_start) AND (download.date <= control.agg_end)) GROUP BY download.country_code, date_trunc('month'::text, (download.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_country_month OWNER TO dspace;

--
-- Name: z_download_unagg_item_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_item_coll_month AS
    SELECT ci.collection_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_item_coll_month OWNER TO dspace;

--
-- Name: z_download_unagg_item_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_item_comm_month AS
    SELECT ci.community_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_item_comm_month OWNER TO dspace;

--
-- Name: z_download_unagg_item_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_item_month AS
    SELECT download.item_id, date_trunc('month'::text, (download.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(download.relative_value) AS relative_value FROM download, control WHERE ((download.date >= control.agg_start) AND (download.date <= control.agg_end)) GROUP BY download.item_id, date_trunc('month'::text, (download.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_item_month OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_coll_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_coll_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, public.collection2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_coll_month_1 OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_coll_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_coll_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, public.collection2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_coll_month_2 OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_comm_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_comm_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, v_communities2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_comm_month_1 OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_comm_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_comm_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, v_communities2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_comm_month_2 OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, control WHERE ((((v.item_id = m.resource_id) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_month_1 OWNER TO dspace;

--
-- Name: z_download_unagg_metadata_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_metadata_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(v.relative_value) AS relative_value FROM download v, public.metadatavalue m, metadata_aggreg g, control WHERE ((((v.item_id = m.resource_id) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_metadata_month_2 OWNER TO dspace;

--
-- Name: z_download_unagg_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_download_unagg_month AS
    SELECT date_trunc('month'::text, (download.date)::timestamp with time zone) AS month_trunc, count(*) AS value, sum(download.relative_value) AS relative_value FROM download, control WHERE ((download.date >= control.agg_start) AND (download.date <= control.agg_end)) GROUP BY date_trunc('month'::text, (download.date)::timestamp with time zone);


ALTER TABLE stats.z_download_unagg_month OWNER TO dspace;

--
-- Name: z_today_date; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_today_date AS
    SELECT ('now'::text)::date AS today_date;


ALTER TABLE stats.z_today_date OWNER TO dspace;

--
-- Name: z_view_unagg_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_coll_month AS
    SELECT ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_coll_month OWNER TO dspace;

--
-- Name: z_view_unagg_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_comm_month AS
    SELECT ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_comm_month OWNER TO dspace;

--
-- Name: z_view_unagg_country_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_country_coll_month AS
    SELECT ci.collection_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_country_coll_month OWNER TO dspace;

--
-- Name: z_view_unagg_country_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_country_comm_month AS
    SELECT ci.community_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, v.country_code, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_country_comm_month OWNER TO dspace;

--
-- Name: z_view_unagg_country_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_country_month AS
    SELECT view.country_code, date_trunc('month'::text, (view.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view, control WHERE ((view.date >= control.agg_start) AND (view.date <= control.agg_end)) GROUP BY view.country_code, date_trunc('month'::text, (view.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_country_month OWNER TO dspace;

--
-- Name: z_view_unagg_item_coll_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_item_coll_month AS
    SELECT ci.collection_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.collection2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.collection_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_item_coll_month OWNER TO dspace;

--
-- Name: z_view_unagg_item_comm_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_item_comm_month AS
    SELECT ci.community_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, v_communities2item ci, control WHERE ((v.item_id = ci.item_id) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY ci.community_id, v.item_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_item_comm_month OWNER TO dspace;

--
-- Name: z_view_unagg_item_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_item_month AS
    SELECT view.item_id, date_trunc('month'::text, (view.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view, control WHERE ((view.date >= control.agg_start) AND (view.date <= control.agg_end)) GROUP BY view.item_id, date_trunc('month'::text, (view.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_item_month OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_coll_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_coll_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, public.collection2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_coll_month_1 OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_coll_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_coll_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, public.collection2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), ci.collection_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_coll_month_2 OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_comm_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_comm_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, v_communities2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_comm_month_1 OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_comm_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_comm_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, v_communities2item ci, control WHERE (((((v.item_id = m.resource_id) AND (v.item_id = ci.item_id)) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), ci.community_id, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_comm_month_2 OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_month_1; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_month_1 AS
    SELECT m.metadata_field_id AS field_id, m.text_value AS field_value, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, control WHERE ((((v.item_id = m.resource_id) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id <> 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, m.text_value, date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_month_1 OWNER TO dspace;

--
-- Name: z_view_unagg_metadata_month_2; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_metadata_month_2 AS
    SELECT m.metadata_field_id AS field_id, substr(m.text_value, 1, 4) AS field_value, date_trunc('month'::text, (v.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view v, public.metadatavalue m, metadata_aggreg g, control WHERE ((((v.item_id = m.resource_id) AND (m.metadata_field_id = g.metadata_field_id)) AND (g.metadata_field_id = 15)) AND ((v.date >= control.agg_start) AND (v.date <= control.agg_end))) GROUP BY m.metadata_field_id, substr(m.text_value, 1, 4), date_trunc('month'::text, (v.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_metadata_month_2 OWNER TO dspace;

--
-- Name: z_view_unagg_month; Type: VIEW; Schema: stats; Owner: dspace
--

CREATE VIEW z_view_unagg_month AS
    SELECT date_trunc('month'::text, (view.date)::timestamp with time zone) AS month_trunc, count(*) AS value FROM view, control WHERE ((view.date >= control.agg_start) AND (view.date <= control.agg_end)) GROUP BY date_trunc('month'::text, (view.date)::timestamp with time zone);


ALTER TABLE stats.z_view_unagg_month OWNER TO dspace;

--
-- Name: agent_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY agent
    ADD CONSTRAINT agent_pkey PRIMARY KEY (agent_id);


--
-- Name: agent_staging_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY agent_staging
    ADD CONSTRAINT agent_staging_pkey PRIMARY KEY (agent_id);


--
-- Name: control_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY control
    ADD CONSTRAINT control_pkey PRIMARY KEY (control_id);


--
-- Name: country_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey PRIMARY KEY (code);


--
-- Name: download_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_coll_month
    ADD CONSTRAINT download_coll_month_pk PRIMARY KEY (collection_id, yearmonth);


--
-- Name: download_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_comm_month
    ADD CONSTRAINT download_comm_month_pk PRIMARY KEY (community_id, yearmonth);


--
-- Name: download_country_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_country_coll_month
    ADD CONSTRAINT download_country_coll_month_pk PRIMARY KEY (country_code, collection_id, yearmonth);


--
-- Name: download_country_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_country_comm_month
    ADD CONSTRAINT download_country_comm_month_pk PRIMARY KEY (country_code, community_id, yearmonth);


--
-- Name: download_country_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_country_month
    ADD CONSTRAINT download_country_month_pk PRIMARY KEY (country_code, yearmonth);


--
-- Name: download_item_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_item_coll_month
    ADD CONSTRAINT download_item_coll_month_pk PRIMARY KEY (item_id, collection_id, yearmonth);


--
-- Name: download_item_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_item_comm_month
    ADD CONSTRAINT download_item_comm_month_pk PRIMARY KEY (item_id, community_id, yearmonth);


--
-- Name: download_item_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_item_month
    ADD CONSTRAINT download_item_month_pk PRIMARY KEY (item_id, yearmonth);


--
-- Name: download_metadata_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_metadata_coll_month
    ADD CONSTRAINT download_metadata_coll_month_pk PRIMARY KEY (field_id, field_value, collection_id, yearmonth);


--
-- Name: download_metadata_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_metadata_comm_month
    ADD CONSTRAINT download_metadata_comm_month_pk PRIMARY KEY (field_id, field_value, community_id, yearmonth);


--
-- Name: download_metadata_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_metadata_month
    ADD CONSTRAINT download_metadata_month_pk PRIMARY KEY (field_id, field_value, yearmonth);


--
-- Name: download_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download_month
    ADD CONSTRAINT download_month_pk PRIMARY KEY (yearmonth);


--
-- Name: download_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY download
    ADD CONSTRAINT download_pkey PRIMARY KEY (download_id);


--
-- Name: ip_institution_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY ip_institution
    ADD CONSTRAINT ip_institution_pkey PRIMARY KEY (ip_range);


--
-- Name: ip_keepwatched_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY ip_keepwatched
    ADD CONSTRAINT ip_keepwatched_pk PRIMARY KEY (ip);


--
-- Name: ip_spider_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY ip_spider
    ADD CONSTRAINT ip_spider_pkey PRIMARY KEY (ip, agent_id);


--
-- Name: language_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);


--
-- Name: login_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY login
    ADD CONSTRAINT login_pkey PRIMARY KEY (login_id);


--
-- Name: metadata_aggreg_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY metadata_aggreg
    ADD CONSTRAINT metadata_aggreg_pk PRIMARY KEY (metadata_field_id);


--
-- Name: month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY month
    ADD CONSTRAINT month_pk PRIMARY KEY (month);


--
-- Name: view_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_coll_month
    ADD CONSTRAINT view_coll_month_pk PRIMARY KEY (collection_id, yearmonth);


--
-- Name: view_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_comm_month
    ADD CONSTRAINT view_comm_month_pk PRIMARY KEY (community_id, yearmonth);


--
-- Name: view_country_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_country_coll_month
    ADD CONSTRAINT view_country_coll_month_pk PRIMARY KEY (country_code, collection_id, yearmonth);


--
-- Name: view_country_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_country_comm_month
    ADD CONSTRAINT view_country_comm_month_pk PRIMARY KEY (country_code, community_id, yearmonth);


--
-- Name: view_country_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_country_month
    ADD CONSTRAINT view_country_month_pk PRIMARY KEY (country_code, yearmonth);


--
-- Name: view_item_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_item_coll_month
    ADD CONSTRAINT view_item_coll_month_pk PRIMARY KEY (item_id, collection_id, yearmonth);


--
-- Name: view_item_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_item_comm_month
    ADD CONSTRAINT view_item_comm_month_pk PRIMARY KEY (item_id, community_id, yearmonth);


--
-- Name: view_item_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_item_month
    ADD CONSTRAINT view_item_month_pk PRIMARY KEY (item_id, yearmonth);


--
-- Name: view_metadata_coll_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_metadata_coll_month
    ADD CONSTRAINT view_metadata_coll_month_pk PRIMARY KEY (field_id, field_value, collection_id, yearmonth);


--
-- Name: view_metadata_comm_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_metadata_comm_month
    ADD CONSTRAINT view_metadata_comm_month_pk PRIMARY KEY (field_value, community_id, yearmonth);


--
-- Name: view_metadata_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_metadata_month
    ADD CONSTRAINT view_metadata_month_pk PRIMARY KEY (field_id, field_value, yearmonth);


--
-- Name: view_month_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view_month
    ADD CONSTRAINT view_month_pk PRIMARY KEY (yearmonth);


--
-- Name: view_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY view
    ADD CONSTRAINT view_pkey PRIMARY KEY (view_id);


--
-- Name: workflow_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY workflow
    ADD CONSTRAINT workflow_pkey PRIMARY KEY (workflow_id);


--
-- Name: workflow_process; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY workflow_intervals
    ADD CONSTRAINT workflow_process PRIMARY KEY (item_id);


--
-- Name: workflow_steps_pkey; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY workflow_steps
    ADD CONSTRAINT workflow_steps_pkey PRIMARY KEY (code);


--
-- Name: year_pk; Type: CONSTRAINT; Schema: stats; Owner: dspace; Tablespace: 
--

ALTER TABLE ONLY year
    ADD CONSTRAINT year_pk PRIMARY KEY (year);


--
-- Name: agent_name; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE UNIQUE INDEX agent_name ON agent USING btree (name);


--
-- Name: down_country_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX down_country_idx ON download USING btree (country_code);


--
-- Name: down_date_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX down_date_idx ON download USING btree (date);


--
-- Name: down_item_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX down_item_idx ON download USING btree (item_id);


--
-- Name: download_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_coll_month_year_i ON download_coll_month USING btree (year);


--
-- Name: download_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_comm_month_year_i ON download_comm_month USING btree (year);


--
-- Name: download_country_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_country_coll_month_year_i ON download_country_coll_month USING btree (year);


--
-- Name: download_country_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_country_comm_month_year_i ON download_country_comm_month USING btree (year);


--
-- Name: download_country_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_country_month_year_i ON download_country_month USING btree (year);


--
-- Name: download_item_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_item_coll_month_year_i ON download_item_coll_month USING btree (year);


--
-- Name: download_item_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_item_comm_month_year_i ON download_item_comm_month USING btree (year);


--
-- Name: download_item_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_item_month_year_i ON download_item_month USING btree (year);


--
-- Name: download_metadata_coll_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_coll_month_field_i ON download_metadata_coll_month USING btree (field_id);


--
-- Name: download_metadata_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_coll_month_year_i ON download_metadata_coll_month USING btree (year);


--
-- Name: download_metadata_comm_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_comm_month_field_i ON download_metadata_comm_month USING btree (field_id);


--
-- Name: download_metadata_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_comm_month_year_i ON download_metadata_comm_month USING btree (year);


--
-- Name: download_metadata_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_month_field_i ON download_metadata_month USING btree (field_id);


--
-- Name: download_metadata_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_metadata_month_year_i ON download_metadata_month USING btree (year);


--
-- Name: download_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX download_month_year_i ON download_month USING btree (year);


--
-- Name: login_date_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX login_date_idx ON login USING btree (date);


--
-- Name: view_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_coll_month_year_i ON view_coll_month USING btree (year);


--
-- Name: view_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_comm_month_year_i ON view_comm_month USING btree (year);


--
-- Name: view_country_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_country_coll_month_year_i ON view_country_coll_month USING btree (year);


--
-- Name: view_country_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_country_comm_month_year_i ON view_country_comm_month USING btree (year);


--
-- Name: view_country_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_country_idx ON view USING btree (country_code);


--
-- Name: view_country_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_country_month_year_i ON view_country_month USING btree (year);


--
-- Name: view_date_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_date_idx ON view USING btree (date);


--
-- Name: view_item_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_item_coll_month_year_i ON view_item_coll_month USING btree (year);


--
-- Name: view_item_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_item_comm_month_year_i ON view_item_comm_month USING btree (year);


--
-- Name: view_item_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_item_idx ON view USING btree (item_id);


--
-- Name: view_item_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_item_month_year_i ON view_item_month USING btree (year);


--
-- Name: view_metadata_coll_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_coll_month_field_i ON view_metadata_coll_month USING btree (field_id);


--
-- Name: view_metadata_coll_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_coll_month_year_i ON view_metadata_coll_month USING btree (year);


--
-- Name: view_metadata_comm_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_comm_month_field_i ON view_metadata_comm_month USING btree (field_id);


--
-- Name: view_metadata_comm_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_comm_month_year_i ON view_metadata_comm_month USING btree (year);


--
-- Name: view_metadata_month_field_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_month_field_i ON view_metadata_month USING btree (field_id);


--
-- Name: view_metadata_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_metadata_month_year_i ON view_metadata_month USING btree (year);


--
-- Name: view_month_year_i; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX view_month_year_i ON view_month USING btree (year);


--
-- Name: workflow_date_idx; Type: INDEX; Schema: stats; Owner: dspace; Tablespace: 
--

CREATE INDEX workflow_date_idx ON workflow USING btree (date);


--
-- Name: $1; Type: FK CONSTRAINT; Schema: stats; Owner: dspace
--

ALTER TABLE ONLY ip_spider
    ADD CONSTRAINT "$1" FOREIGN KEY (agent_id) REFERENCES agent(agent_id);


--
-- Name: $1; Type: FK CONSTRAINT; Schema: stats; Owner: dspace
--

ALTER TABLE ONLY login
    ADD CONSTRAINT "$1" FOREIGN KEY (country_code) REFERENCES country(code);


--
-- Name: $1; Type: FK CONSTRAINT; Schema: stats; Owner: dspace
--

ALTER TABLE ONLY download
    ADD CONSTRAINT "$1" FOREIGN KEY (country_code) REFERENCES country(code);


--
-- Name: $1; Type: FK CONSTRAINT; Schema: stats; Owner: dspace
--

ALTER TABLE ONLY view
    ADD CONSTRAINT "$1" FOREIGN KEY (country_code) REFERENCES country(code);


--
-- Name: ip_spider_country_fk; Type: FK CONSTRAINT; Schema: stats; Owner: dspace
--

ALTER TABLE ONLY ip_spider
    ADD CONSTRAINT ip_spider_country_fk FOREIGN KEY (country_code) REFERENCES country(code);


CREATE TABLE stats.view_count (
    item_id integer,
    count integer
);

CREATE TABLE stats.download_count (
    item_id integer,
    count integer
);

CREATE TABLE stats.author_item (
    author TEXT, 
    item_id integer
);
    
--
GRANT ALL ON SCHEMA stats TO dspace;
GRANT USAGE ON SCHEMA stats TO PUBLIC;


--
-- PostgreSQL database dump complete
--


 
 insert into stats.language values ('eng','English');
 insert into stats.language values ('por','Portuguese');
 insert into stats.language values ('fra','French');
 insert into stats.language values ('spa','Spanish');
 insert into stats.language values ('deu','German');
 insert into stats.language values ('ita','Italian');
 insert into stats.language values ('pol','Polish');
 insert into stats.language values ('nor','Norwegian');

 

insert into stats.metadata_aggreg values (53);
insert into stats.metadata_aggreg values (66);
insert into stats.metadata_aggreg values (3);
insert into stats.metadata_aggreg values (15);

insert into stats.month values ('01');
insert into stats.month values ('02');
insert into stats.month values ('03');
insert into stats.month values ('04');
insert into stats.month values ('05');
insert into stats.month values ('06');
insert into stats.month values ('07');
insert into stats.month values ('08');
insert into stats.month values ('09');
insert into stats.month values ('10');
insert into stats.month values ('11');
insert into stats.month values ('12');

insert into stats.year values (2000);
insert into stats.year values (2001);
insert into stats.year values (2002);
insert into stats.year values (2003);
insert into stats.year values (2004);
insert into stats.year values (2005);
insert into stats.year values (2006);
insert into stats.year values (2007);
insert into stats.year values (2008);
insert into stats.year values (2009);
insert into stats.year values (2010);
insert into stats.year values (2011);
insert into stats.year values (2012);
insert into stats.year values (2013);
insert into stats.year values (2014);
insert into stats.year values (2015);

insert into stats.control values (1,null,to_date('01011900','ddmmyyyy'));

insert into stats.workflow_steps values (0, 'Submission', true);
insert into stats.workflow_steps values (1, 'Step 1 (Review)', false);
insert into stats.workflow_steps values (2, 'Step 1 (Review)', true);
insert into stats.workflow_steps values (3, 'Step 2 (Check)', false);
insert into stats.workflow_steps values (4, 'Step 2 (Check)', true);
insert into stats.workflow_steps values (5, 'Step 3 (Final Edit)', false);
insert into stats.workflow_steps values (6, 'Step 3 (Final Edit)', true);
insert into stats.workflow_steps values (10, 'Batch submission', true);
insert into stats.workflow_steps values (20, 'Sword submission', true);

insert into stats.temp values ('0.1_hseo(at)cs.rutgers.edu');
insert into stats.temp values ('192.comAgent');
insert into stats.temp values ('4anything.com');
insert into stats.temp values ('AbachoBOT');
insert into stats.temp values ('ABCdatos');
insert into stats.temp values ('abcdatos_botlink');
insert into stats.temp values ('abot/0.1');
insert into stats.temp values ('About/0.1libwww-perl/5.47');
insert into stats.temp values ('accoona');
insert into stats.temp values ('Accoona');
insert into stats.temp values ('AcoiRobot');
insert into stats.temp values ('Acoon');
insert into stats.temp values ('Acorn');
insert into stats.temp values ('admin@crawler.de');
insert into stats.temp values ('AESOP');
insert into stats.temp values ('AESOP_com_SpiderMan');
insert into stats.temp values ('Agadine');
insert into stats.temp values ('Agent-admin/');
insert into stats.temp values ('ah-ha.com crawler');
insert into stats.temp values ('AIBOT');
insert into stats.temp values ('aipbot');
insert into stats.temp values ('Aipbot');
insert into stats.temp values ('Aladin');
insert into stats.temp values ('Aleksika');
insert into stats.temp values ('AlkalineBOT');
insert into stats.temp values ('Allesklar');
insert into stats.temp values ('AltaVista');
insert into stats.temp values ('Amfibi');
insert into stats.temp values ('AmfibiBOT');
insert into stats.temp values ('Amibot');
insert into stats.temp values ('Amiga-AWeb/3.4.167SE');
insert into stats.temp values ('amzn_assoc');
insert into stats.temp values ('AnnoMille');
insert into stats.temp values ('AnswerBus');
insert into stats.temp values ('AnswerChase');
insert into stats.temp values ('AnzwersCrawl');
insert into stats.temp values ('A-Online Search');
insert into stats.temp values ('Apexoo');
insert into stats.temp values ('Aport');
insert into stats.temp values ('appie');
insert into stats.temp values ('Appie');
insert into stats.temp values ('Arachnoidea');
insert into stats.temp values ('arachnoidea@euroseek.net');
insert into stats.temp values ('Aranha');
insert into stats.temp values ('ArchitectSpider');
insert into stats.temp values ('ArchitextSpider');
insert into stats.temp values ('archive_org');
insert into stats.temp values ('archive.org');
insert into stats.temp values ('archive.org_bot');
insert into stats.temp values ('Arikus_Spider');
insert into stats.temp values ('ASAHA');
insert into stats.temp values ('Asahina');
insert into stats.temp values ('AskAboutOil');
insert into stats.temp values ('Asked');
insert into stats.temp values ('ask jeeves');
insert into stats.temp values ('ASPseek');
insert into stats.temp values ('ASPSeek');
insert into stats.temp values ('Asterias');
insert into stats.temp values ('Atlocal');
insert into stats.temp values ('AtlocalBot');
insert into stats.temp values ('Atomz');
insert into stats.temp values ('autohttp');
insert into stats.temp values ('AV Fetch 1.0');
insert into stats.temp values ('AVSearch');
insert into stats.temp values ('Axadine');
insert into stats.temp values ('axadine/  (Axadine Crawler; http://www.axada.de/;  )');
insert into stats.temp values ('AxmoRobot');
insert into stats.temp values ('BaboomBot');
insert into stats.temp values ('Baiduspider');
insert into stats.temp values ('BaiDuSpider');
insert into stats.temp values ('Balihoo');
insert into stats.temp values ('BanBots/1.2');
insert into stats.temp values ('BarraHomeCrawler');
insert into stats.temp values ('Bdcindexer');
insert into stats.temp values ('bdcindexer_2.6.2');
insert into stats.temp values ('BDFetch');
insert into stats.temp values ('BDNcentral Crawler v2.3');
insert into stats.temp values ('Beautybot');
insert into stats.temp values ('beautybot/1.0');
insert into stats.temp values ('BebopBot');
insert into stats.temp values ('BecomeBot');
insert into stats.temp values ('BigCliqueBOT');
insert into stats.temp values ('BigCliqueBOT/1.03-dev');
insert into stats.temp values ('BIGLOTRON');
insert into stats.temp values ('Bigsearch.ca');
insert into stats.temp values ('Bilbo');
insert into stats.temp values ('Bilgi');
insert into stats.temp values ('BilgiBetaBot');
insert into stats.temp values ('Bitacle');
insert into stats.temp values ('Blaiz-Bee');
insert into stats.temp values ('BlitzBOT');
insert into stats.temp values ('BlogBot');
insert into stats.temp values ('Bloglines');
insert into stats.temp values ('Blogpulse');
insert into stats.temp values ('BlogSearch');
insert into stats.temp values ('BlogsNowBot');
insert into stats.temp values ('blogWatcher_Spider');
insert into stats.temp values ('BlogzIce');
insert into stats.temp values ('boitho.com');
insert into stats.temp values ('BotSeer');
insert into stats.temp values ('BravoBrian');
insert into stats.temp values ('BruinBot');
insert into stats.temp values ('BSDSeek/1.0');
insert into stats.temp values ('BTbot');
insert into stats.temp values ('BuildCMS');
insert into stats.temp values ('BullsEye');
insert into stats.temp values ('bumblebee@relevare.com');
insert into stats.temp values ('BurstFindCrawler');
insert into stats.temp values ('Buscaplus');
insert into stats.temp values ('Carleson');
insert into stats.temp values ('carleson/1.0');
insert into stats.temp values ('Carnegie_Mellon_University');
insert into stats.temp values ('Catall Spider');
insert into stats.temp values ('Ccubee');
insert into stats.temp values ('cfetch/1.0');
insert into stats.temp values ('CipinetBot');
insert into stats.temp values ('ClariaBot/1.0');
insert into stats.temp values ('Claymont.com');
insert into stats.temp values ('CloakDetect');
insert into stats.temp values ('Clushbot');
insert into stats.temp values ('ActiveWorlds/3.');
insert into stats.temp values ('collage.cgi');
insert into stats.temp values ('cometrics-bot');
insert into stats.temp values ('Computer_and_Automation_Research');
insert into stats.temp values ('Comrite');
insert into stats.temp values ('contact/jylee@kies.co.kr');
insert into stats.temp values ('Convera');
insert into stats.temp values ('CoolBot');
insert into stats.temp values ('Cosmos');
insert into stats.temp values ('CougarSearch');
insert into stats.temp values ('Cowbot');
insert into stats.temp values ('CrawlConvera');
insert into stats.temp values ('Crawler');
insert into stats.temp values ('CrawlerBoy');
insert into stats.temp values ('crawler@brainbot.com');
insert into stats.temp values ('Crawler (cometsearch@cometsystems.com)');
insert into stats.temp values ('crawler@fast.no');
insert into stats.temp values ('Crawllybot');
insert into stats.temp values ('CreativeCommons');
insert into stats.temp values ('CrocCrawler');
insert into stats.temp values ('Custom Spider www.bisnisseek.com');
insert into stats.temp values ('CydralSpider');
insert into stats.temp values ('DaAdLe.com ROBOT/');
insert into stats.temp values ('DataFountains');
insert into stats.temp values ('DataparkSearch');
insert into stats.temp values ('DataSpear');
insert into stats.temp values ('DaviesBot');
insert into stats.temp values ('DaviesBot/1.7');
insert into stats.temp values ('dbDig');
insert into stats.temp values ('dCSbot/1.1');
insert into stats.temp values ('DeepIndex');
insert into stats.temp values ('DeepIndexer.ca');
insert into stats.temp values ('deepweb');
insert into stats.temp values ('Demo Bot DOT 16b');
insert into stats.temp values ('de.searchengine.comBot');
insert into stats.temp values ('dev-spider2.searchpsider.com');
insert into stats.temp values ('Diamond');
insert into stats.temp values ('Digger');
insert into stats.temp values ('Digimarc WebReader');
insert into stats.temp values ('DigOut4U');
insert into stats.temp values ('DittoSpyder');
insert into stats.temp values ('dloader(NaverRobot)/');
insert into stats.temp values ('DoCoMo');
insert into stats.temp values ('DreamCatcher');
insert into stats.temp values ('Drecombot');
insert into stats.temp values ('dtSearchSpider');
insert into stats.temp values ('Dumbot');
insert into stats.temp values ('dumrobo(NaverRobot)/');
insert into stats.temp values ('EARTHCOM');
insert into stats.temp values ('EasyDL');
insert into stats.temp values ('eBot');
insert into stats.temp values ('EchO!');
insert into stats.temp values ('egothor/3.0a (+http://www.xdefine.org/robot.html)');
insert into stats.temp values ('ejupiter');
insert into stats.temp values ('elfbot');
insert into stats.temp values ('EMPAS');
insert into stats.temp values ('Enterprise_Search');
insert into stats.temp values ('Envolk');
insert into stats.temp values ('erik@malfunction.org');
insert into stats.temp values ('EroCrawler');
insert into stats.temp values ('eseek-larbin_2.6.2');
insert into stats.temp values ('ESISmartSpider');
insert into stats.temp values ('ES.NET_Crawler');
insert into stats.temp values ('e-SocietyRobot');
insert into stats.temp values ('eStyleSearch 4');
insert into stats.temp values ('EuripBot');
insert into stats.temp values ('EvaalSE');
insert into stats.temp values ('Eventax');
insert into stats.temp values ('Everest-Vulcan');
insert into stats.temp values ('Everest-Vulcan Inc./0.1');
insert into stats.temp values ('Exabot');
insert into stats.temp values ('ExactSeek');
insert into stats.temp values ('exactseek-crawler-2.63');
insert into stats.temp values ('Exalead NG/MimeLive Client');
insert into stats.temp values ('Excalibur Internet Spider V6.5.4');
insert into stats.temp values ('Execrawl');
insert into stats.temp values ('ExperimentalHenrytheMiragoRobot');
insert into stats.temp values ('EyeCatcher');
insert into stats.temp values ('EZResult');
insert into stats.temp values ('Factbot');
insert into stats.temp values ('Fastbot');
insert into stats.temp values ('FastCrawler');
insert into stats.temp values ('Fast Crawler Gold Edition');
insert into stats.temp values ('FAST Data Search Crawler');
insert into stats.temp values ('FAST Enterprise Crawler');
insert into stats.temp values ('FAST FirstPage retriever');
insert into stats.temp values ('FAST MetaWeb Crawler');
insert into stats.temp values ('Fast PartnerSite Crawler');
insert into stats.temp values ('FastSearch-AllTheWeb.com');
insert into stats.temp values ('FAST-WebCrawler');
insert into stats.temp values ('favo.eu');
insert into stats.temp values ('Faxobot');
insert into stats.temp values ('Feed24.com');
insert into stats.temp values ('Feedfetcher-Google');
insert into stats.temp values ('Feed Seeker Bot');
insert into stats.temp values ('Feedster Crawler');
insert into stats.temp values ('Felix');
insert into stats.temp values ('Filangy');
insert into stats.temp values ('FindAnISP.com');
insert into stats.temp values ('Findexa');
insert into stats.temp values ('FineBot');
insert into stats.temp values ('Firefly');
insert into stats.temp values ('FirstGov.gov');
insert into stats.temp values ('Firstsbot');
insert into stats.temp values ('Flapbot');
insert into stats.temp values ('Flatlandbot');
insert into stats.temp values ('Fluffy the spider');
insert into stats.temp values ('Flunky');
insert into stats.temp values ('FocusedSampler/1.0');
insert into stats.temp values ('Fooky.com');
insert into stats.temp values ('Francis');
insert into stats.temp values ('Francis/1.0');
insert into stats.temp values ('FreeFind');
insert into stats.temp values ('FreshNotes');
insert into stats.temp values ('FuseBulb');
insert into stats.temp values ('FyberSearch');
insert into stats.temp values ('FyberSpider');
insert into stats.temp values ('Gaisbot');
insert into stats.temp values ('GAIS Robot');
insert into stats.temp values ('GalaxyBot');
insert into stats.temp values ('Gallent Search Spider');
insert into stats.temp values ('Gamekitbot');
insert into stats.temp values ('GammaSpider');
insert into stats.temp values ('gazz/1.0');
insert into stats.temp values ('gazz@nttrd.com');
insert into stats.temp values ('GenCrawler');
insert into stats.temp values ('generic_crawler/01.0217/');
insert into stats.temp values ('genieBot');
insert into stats.temp values ('GeonaBot');
insert into stats.temp values ('GigaBaz');
insert into stats.temp values ('GigaBlast');
insert into stats.temp values ('Gigabot');
insert into stats.temp values ('GigabotSiteSearch/2.0');
insert into stats.temp values ('Giskard');
insert into stats.temp values ('GNODSPIDER');
insert into stats.temp values ('Goblin');
insert into stats.temp values ('GoForIt');
insert into stats.temp values ('Gonzo');
insert into stats.temp values ('gonzo1');
insert into stats.temp values ('Goofer/0.2');
insert into stats.temp values ('Googlebot');
insert into stats.temp values ('googlebot@googlebot.com');
insert into stats.temp values ('Googlebot-Image/1.0');
insert into stats.temp values ('Googlebot/Test');
insert into stats.temp values ('GrigorBot');
insert into stats.temp values ('Gromit');
insert into stats.temp values ('grub-client');
insert into stats.temp values ('grub crawler(http://www.grub.org)');
insert into stats.temp values ('gsa-crawler');
insert into stats.temp values ('Gulliver');
insert into stats.temp values ('GurujiBot');
insert into stats.temp values ('HappyFunBot');
insert into stats.temp values ('Harvest-NG');
insert into stats.temp values ('Hatena');
insert into stats.temp values ('hbtronix.spider');
insert into stats.temp values ('HeinrichderMirago');
insert into stats.temp values ('Helix');
insert into stats.temp values ('HenriLeRobotMirago');
insert into stats.temp values ('HenrytheMirago');
insert into stats.temp values ('HenryTheMiragoRobot');
insert into stats.temp values ('Hippias');
insert into stats.temp values ('Hitwise Spider');
insert into stats.temp values ('holmes/');
insert into stats.temp values ('HomePageSearch(hpsearch.uni-trier.de)');
insert into stats.temp values ('Homerbot');
insert into stats.temp values ('Honda-Search');
insert into stats.temp values ('http://Ask.24x.Info/ (http://narres.it/)');
insert into stats.temp values ('http://www.abcdatos.com/botlink/');
insert into stats.temp values ('http://www.almaden.ibm.com');
insert into stats.temp values ('http://www.istarthere.com');
insert into stats.temp values ('http://www.monogol.de');
insert into stats.temp values ('http://www.trendtech.dk/spider.asp');
insert into stats.temp values ('Hubater');
insert into stats.temp values ('i1searchbot');
insert into stats.temp values ('ia_archiver');
insert into stats.temp values ('IAArchiver-1.0');
insert into stats.temp values ('iCCrawler');
insert into stats.temp values ('ichiro');
insert into stats.temp values ('IconSurf');
insert into stats.temp values ('ICRA_label_spider');
insert into stats.temp values ('icsbot-0.1');
insert into stats.temp values ('Ideare - SignSite');
insert into stats.temp values ('ideare - SignSite/1.x');
insert into stats.temp values ('IIITBOT');
insert into stats.temp values ('Ilial');
insert into stats.temp values ('IlseBot');
insert into stats.temp values ('IlTrovatore');
insert into stats.temp values ('ImageWalker');
insert into stats.temp values ('IncyWincy');
insert into stats.temp values ('IndexTheWeb.com');
insert into stats.temp values ('Inet library');
insert into stats.temp values ('InfoFly/1.0');
insert into stats.temp values ('INFOMINE');
insert into stats.temp values ('info@searchhippo.com');
insert into stats.temp values ('InfoSeek');
insert into stats.temp values ('htdig/3.1.');
insert into stats.temp values ('INGRID/3.0 MT');
insert into stats.temp values ('Inktomi');
insert into stats.temp values ('InnerpriseBot');
insert into stats.temp values ('Insitor');
insert into stats.temp values ('Insitornaut');
insert into stats.temp values ('Intelix');
insert into stats.temp values ('InternetArchive');
insert into stats.temp values ('Internet Ninja x.0');
insert into stats.temp values ('InternetSeer');
insert into stats.temp values ('ipiumBot');
insert into stats.temp values ('IPiumBot laurion(dot)com');
insert into stats.temp values ('IpselonBot');
insert into stats.temp values ('IRLbot');
insert into stats.temp values ('Iron33');
insert into stats.temp values ('Jabot');
insert into stats.temp values ('Jack');
insert into stats.temp values ('Jambot');
insert into stats.temp values ('jan.gelin@av.com');
insert into stats.temp values ('Jayde Crawler');
insert into stats.temp values ('jeeves');
insert into stats.temp values ('Jetbot');
insert into stats.temp values ('jyxobot');
insert into stats.temp values ('KE_1.0/2.0');
insert into stats.temp values ('Kenjin Spider');
insert into stats.temp values ('KFSW-Bot');
insert into stats.temp values ('Kinja');
insert into stats.temp values ('KIT-Fireball');
insert into stats.temp values ('KIT-Fireball/2.0');
insert into stats.temp values ('Knowledge.com');
insert into stats.temp values ('Kuloko');
insert into stats.temp values ('kulturarw3');
insert into stats.temp values ('LapozzBot');
insert into stats.temp values ('LEIA/');
insert into stats.temp values ('Libby_1.1/libwww-perl/5.47');
insert into stats.temp values ('libWeb/clsHTTP -- hiongun@kt.co.kr');
insert into stats.temp values ('linknzbot');
insert into stats.temp values ('LiveTrans');
insert into stats.temp values ('Llaut');
insert into stats.temp values ('LNSpiderguy');
insert into stats.temp values ('LocalBot/1.0 ( http://www.localbot.co.uk/)');
insert into stats.temp values ('LocalcomBot');
insert into stats.temp values ('Lockstep Spider');
insert into stats.temp values ('Look.com');
insert into stats.temp values ('lycos');
insert into stats.temp values ('Lycos_Spider');
insert into stats.temp values ('Mackster');
insert into stats.temp values ('Mag-Net');
insert into stats.temp values ('mailto:webcraft@bea.com');
insert into stats.temp values ('mammoth/1.0');
insert into stats.temp values ('MantraAgent');
insert into stats.temp values ('MapoftheInternet.com');
insert into stats.temp values ('mapper@teradex.com');
insert into stats.temp values ('Mariner/5.1b');
insert into stats.temp values ('Martini');
insert into stats.temp values ('MaSagool');
insert into stats.temp values ('MasterSeek');
insert into stats.temp values ('Mata Hari/2.00');
insert into stats.temp values ('Matrix S.p.A. - FAST Enterprise Crawler 6');
insert into stats.temp values ('Maxomobot');
insert into stats.temp values ('MediaCrawler');
insert into stats.temp values ('Mediapartners');
insert into stats.temp values ('Mediapartners-Google/2.1');
insert into stats.temp values ('MediaSearch/0.1');
insert into stats.temp values ('MegaSheep v1.0');
insert into stats.temp values ('Megite');
insert into stats.temp values ('Mercator');
insert into stats.temp values ('Metaeuro');
insert into stats.temp values ('MetagerBot');
insert into stats.temp values ('Metaspinner');
insert into stats.temp values ('Metaspinner/0.01');
insert into stats.temp values ('Metatagsdir');
insert into stats.temp values ('MFC_Tear_Sample');
insert into stats.temp values ('MicroBaz');
insert into stats.temp values ('MicrosoftPrototypeCrawler');
insert into stats.temp values ('Misterbot');
insert into stats.temp values ('Miva');
insert into stats.temp values ('Miva (AlgoFeedback@miva.com)');
insert into stats.temp values ('MJ12bot');
insert into stats.temp values ('MnogoSearch');
insert into stats.temp values ('moget@goo.ne.jp');
insert into stats.temp values ('mogimogi');
insert into stats.temp values ('MojeekBot');
insert into stats.temp values ('Mole2Morris - Mixcat Crawler (+http://mixcat.com)');
insert into stats.temp values ('Morris');
insert into stats.temp values ('mozDex');
insert into stats.temp values ('MP3Bot');
insert into stats.temp values ('MQbot');
insert into stats.temp values ('msnbot');
insert into stats.temp values ('Msnbot');
insert into stats.temp values ('MSNBOT/0.1');
insert into stats.temp values ('MSNPTC');
insert into stats.temp values ('MultiText');
insert into stats.temp values ('MusicWalker');
insert into stats.temp values ('Mylinea.com');
insert into stats.temp values ('Naamah');
insert into stats.temp values ('NABOT');
insert into stats.temp values ('NationalDirectory');
insert into stats.temp values ('NaverBot');
insert into stats.temp values ('NavissoBot');
insert into stats.temp values ('Nazilla');
insert into stats.temp values ('NCSA');
insert into stats.temp values ('Nebullabot');
insert into stats.temp values ('Netluchs');
insert into stats.temp values ('NetResearchServer');
insert into stats.temp values ('NetWhatCrawler');
insert into stats.temp values ('NextopiaBOT');
insert into stats.temp values ('NG/1.0');
insert into stats.temp values ('NG/4.0.1229');
insert into stats.temp values ('NG-Search');
insert into stats.temp values ('Noago');
insert into stats.temp values ('NokodoBot');
insert into stats.temp values ('Norbert the Spider(Burf.com)');
insert into stats.temp values ('Noxtrumbot');
insert into stats.temp values ('NP/0.1');
insert into stats.temp values ('NPBot');
insert into stats.temp values ('nuSearch');
insert into stats.temp values ('Nutch');
insert into stats.temp values ('NutchCVS/0.0x-dev');
insert into stats.temp values ('NutchOrg');
insert into stats.temp values ('NZBot');
insert into stats.temp values ('obidos-bot');
insert into stats.temp values ('ObjectsSearch');
insert into stats.temp values ('oBot');
insert into stats.temp values ('Ocelli');
insert into stats.temp values ('Octora');
insert into stats.temp values ('OliverPerry');
insert into stats.temp values ('omgilibot');
insert into stats.temp values ('OmniExplorer_Bot');
insert into stats.temp values ('Onet.pl');
insert into stats.temp values ('OntoSpider');
insert into stats.temp values ('Openbot');
insert into stats.temp values ('Opencola');
insert into stats.temp values ('Openfind');
insert into stats.temp values ('OpenISearch');
insert into stats.temp values ('OpenTaggerBot');
insert into stats.temp values ('OpenTextSiteCrawler');
insert into stats.temp values ('OpenWebSpider');
insert into stats.temp values ('Oracle Ultra Search');
insert into stats.temp values ('Overture-WebCrawler/');
insert into stats.temp values ('PageBites');
insert into stats.temp values ('Pagebull');
insert into stats.temp values ('parallelContextFocusCrawler');
insert into stats.temp values ('ParaSite');
insert into stats.temp values ('Patwebbot');
insert into stats.temp values ('pd02_1.0.0 pd02_1.0.0@dzimi@post.sk');
insert into stats.temp values ('peerbot');
insert into stats.temp values ('PEERbot');
insert into stats.temp values ('phortse@hanmail.net');
insert into stats.temp values ('PicoSearch');
insert into stats.temp values ('Piffany');
insert into stats.temp values ('pipeLiner');
insert into stats.temp values ('Pizilla++ ver 2.45');
insert into stats.temp values ('PJspider');
insert into stats.temp values ('PluckFeedCrawler');
insert into stats.temp values ('Pompos');
insert into stats.temp values ('Popdex');
insert into stats.temp values ('PortalBSpider/2.0');
insert into stats.temp values ('PROve AnswerBot');
insert into stats.temp values ('psbot');
insert into stats.temp values ('Psbot');
insert into stats.temp values ('psbot/0.1');
insert into stats.temp values ('Qango.com');
insert into stats.temp values ('QPCreep Test Rig');
insert into stats.temp values ('Quepasa');
insert into stats.temp values ('QuepasaCreep');
insert into stats.temp values ('Rabaz');
insert into stats.temp values ('rabaz (rabaz at gigabaz dot com)');
insert into stats.temp values ('RaBot');
insert into stats.temp values ('RAMPyBot');
insert into stats.temp values ('ReadABlog');
insert into stats.temp values ('Reaper');
insert into stats.temp values ('RixBot');
insert into stats.temp values ('roach.smo.av.com-1.0');
insert into stats.temp values ('RoboCrawl');
insert into stats.temp values ('RoboPal');
insert into stats.temp values (':robot/1.0');
insert into stats.temp values ('Robot@SuperSnooper.Com');
insert into stats.temp values ('Robot/www.pj-search.com');
insert into stats.temp values ('robot@xyleme.com');
insert into stats.temp values ('Robozilla/1.0');
insert into stats.temp values ('Rotondo/3.1 libwww/5.3.1');
insert into stats.temp values ('RRC (crawler_admin@bigfoot.com)');
insert into stats.temp values ('RSSMicro');
insert into stats.temp values ('RufusBot');
insert into stats.temp values ('ru-robot');
insert into stats.temp values ('SandCrawler');
insert into stats.temp values ('Savvybot');
insert into stats.temp values ('SBIder');
insert into stats.temp values ('ScanWeb');
insert into stats.temp values ('ScholarUniverse');
insert into stats.temp values ('schwarzmann.biz');
insert into stats.temp values ('ScollSpider');
insert into stats.temp values ('Scooter');
insert into stats.temp values ('scooter-venus-3.0.vns');
insert into stats.temp values ('ScoutAbout');
insert into stats.temp values ('Scoutmaster');
insert into stats.temp values ('Scrubby');
insert into stats.temp values ('search.at V1.2');
insert into stats.temp values ('search.ch');
insert into stats.temp values ('SearchdayBot');
insert into stats.temp values ('SearchExpress');
insert into stats.temp values ('SearchGuild');
insert into stats.temp values ('SearchSight');
insert into stats.temp values ('SearchSpider');
insert into stats.temp values ('SearchTone');
insert into stats.temp values ('sebastien.ailleret@inria.fr');
insert into stats.temp values ('Seekbot');
insert into stats.temp values ('Seeker.lookseek.com');
insert into stats.temp values ('Sensis');
insert into stats.temp values ('Seznam');
insert into stats.temp values ('SeznamBot');
insert into stats.temp values ('ShopWiki');
insert into stats.temp values ('ShopWiki/1.0');
insert into stats.temp values ('Shoula.com');
insert into stats.temp values ('SightQuestBot/');
insert into stats.temp values ('silk/1.0');
insert into stats.temp values ('SiteSpider');
insert into stats.temp values ('SiteTruth.com');
insert into stats.temp values ('SiteXpert');
insert into stats.temp values ('Skampy');
insert into stats.temp values ('Skimpy');
insert into stats.temp values ('Slarp/0.1');
insert into stats.temp values ('Slider_Search_v1-de');
insert into stats.temp values ('Slurp');
insert into stats.temp values ('slurp@inktomi');
insert into stats.temp values ('SlySearch');
insert into stats.temp values ('SnykeBot');
insert into stats.temp values ('Speedfind');
insert into stats.temp values ('Speedy Spider');
insert into stats.temp values ('Speedy_Spider');
insert into stats.temp values ('Spida/0.1');
insert into stats.temp values ('spider@aeneid.com');
insert into stats.temp values ('SpiderMan');
insert into stats.temp values ('SpiderMonkey');
insert into stats.temp values ('Spider-Sleek');
insert into stats.temp values ('Spider TraficDublu');
insert into stats.temp values ('spider.yellopet.com');
insert into stats.temp values ('sportsuchmaschine.de');
insert into stats.temp values ('sproose');
insert into stats.temp values ('Sqworm');
insert into stats.temp values ('StackRambler');
insert into stats.temp values ('Steeler');
insert into stats.temp values ('Strategic Board Bot');
insert into stats.temp values ('suchbaer.de');
insert into stats.temp values ('suchbot');
insert into stats.temp values ('Suchbot');
insert into stats.temp values ('Suchknecht.at');
insert into stats.temp values ('suchpad/1.0');
insert into stats.temp values ('Suchpadbot');
insert into stats.temp values ('support@canseek.ca');
insert into stats.temp values ('Swooglebot');
insert into stats.temp values ('SygolBot');
insert into stats.temp values ('SynoBot');
insert into stats.temp values ('Synoo');
insert into stats.temp values ('Syntryx');
insert into stats.temp values ('Szukacz');
insert into stats.temp values ('tags2dir');
insert into stats.temp values ('Talkro Web-Shot');
insert into stats.temp values ('TCDBOT');
insert into stats.temp values ('TECOMAC-Crawler');
insert into stats.temp values ('Tecomi Bot');
insert into stats.temp values ('Teoma');
insert into stats.temp values ('teoma_admin@hawkholdings.com');
insert into stats.temp values ('teoma_agent1');
insert into stats.temp values ('Teradex_Mapper');
insert into stats.temp values ('TerrawizBot');
insert into stats.temp values ('TheSuBot');
insert into stats.temp values ('thumbshots-de');
insert into stats.temp values ('TJG/Spider');
insert into stats.temp values ('Tkensaku');
insert into stats.temp values ('Topodia');
insert into stats.temp values ('Toutatis');
insert into stats.temp values ('Traazibot');
insert into stats.temp values ('Trampelpfad');
insert into stats.temp values ('Tumblr');
insert into stats.temp values ('Turnitin');
insert into stats.temp values ('TurnitinBot');
insert into stats.temp values ('TutorGig');
insert into stats.temp values ('Tutorial Crawler');
insert into stats.temp values ('Tv<nn>_Merc_resh_26_1_D-1.0');
insert into stats.temp values ('twiceler');
insert into stats.temp values ('Tygo');
insert into stats.temp values ('TygoBot');
insert into stats.temp values ('UCmore');
insert into stats.temp values ('UdmSearch');
insert into stats.temp values ('UK Searcher Spider');
insert into stats.temp values ('UKWizz');
insert into stats.temp values ('Ultraseek');
insert into stats.temp values ('Updated');
insert into stats.temp values ('updated/0.1beta');
insert into stats.temp values ('UptimeBot');
insert into stats.temp values ('URLBlaze');
insert into stats.temp values ('URL_Spider_Pro');
insert into stats.temp values ('USyd-NLP-Spider');
insert into stats.temp values ('Vagabondo');
insert into stats.temp values ('Vakes');
insert into stats.temp values ('VeryGoodSearch');
insert into stats.temp values ('verzamelgids.nl');
insert into stats.temp values ('Vespa Crawler');
insert into stats.temp values ('Visbot');
insert into stats.temp values ('VisBot');
insert into stats.temp values ('VMBot');
insert into stats.temp values ('Voyager');
insert into stats.temp values ('VSE/1.0');
insert into stats.temp values ('vspider');
insert into stats.temp values ('Vspider');
insert into stats.temp values ('W3SiteSearch');
insert into stats.temp values ('Wavefire');
insert into stats.temp values ('Waypath');
insert into stats.temp values ('WebAlta');
insert into stats.temp values ('WebarooBot');
insert into stats.temp values ('Webclipping.com');
insert into stats.temp values ('webcollage');
insert into stats.temp values ('WebCorp');
insert into stats.temp values ('webcrawl.net');
insert into stats.temp values ('WebFindBot');
insert into stats.temp values ('Weblog Attitude Diffusion');
insert into stats.temp values ('webmeasurement-bot, http://rvs.informatik.uni-leipzig.de');
insert into stats.temp values ('WebRankSpider');
insert into stats.temp values ('WebSearch.COM.AU');
insert into stats.temp values ('WebsiteWorth');
insert into stats.temp values ('Webspinne');
insert into stats.temp values ('Websquash.com');
insert into stats.temp values ('Webverzeichnis.de');
insert into stats.temp values ('whatuseek');
insert into stats.temp values ('whatUseek');
insert into stats.temp values ('whatUseek_winona/3.0');
insert into stats.temp values ('WhizBang! Lab');
insert into stats.temp values ('Willow Internet Crawler by Twotrees');
insert into stats.temp values ('WinkBot');
insert into stats.temp values ('wisenutbot');
insert into stats.temp values ('Worio');
insert into stats.temp values ('Woriobot');
insert into stats.temp values ('WorldLight');
insert into stats.temp values ('Wotbox');
insert into stats.temp values ('wume_crawler');
insert into stats.temp values ('www.arianna.it');
insert into stats.temp values ('WWWeasel');
insert into stats.temp values ('www.inktomisearch.com');
insert into stats.temp values ('www.WebWombat.com.au');
insert into stats.temp values ('X-Crawler');
insert into stats.temp values ('xirq/0.1-beta');
insert into stats.temp values ('xyro_(xcrawler@cosmos.inria.fr)');
insert into stats.temp values ('Yacy');
insert into stats.temp values ('Yahoo');
insert into stats.temp values ('Yahoo-Blogs/v3.9');
insert into stats.temp values ('YahooSeeker/CafeKelsa');
insert into stats.temp values ('Yandex');
insert into stats.temp values ('Y!J');
insert into stats.temp values ('YodaoBot');
insert into stats.temp values ('Yoogli');
insert into stats.temp values ('Yoono');
insert into stats.temp values ('ZACATEK ');
insert into stats.temp values ('Zearchit');
insert into stats.temp values ('Zerxbot');
insert into stats.temp values ('Zeusbot');
insert into stats.temp values ('Zspider');
insert into stats.temp values ('ZyBorg');
insert into stats.temp values ('agadine/1.');
insert into stats.temp values ('crawler@');
insert into stats.temp values ('Crawler V 0.2');
insert into stats.temp values ('Iltrovatore-Setaccio/');
insert into stats.temp values ('larbin_2');
insert into stats.temp values ('moget/');
insert into stats.temp values ('MSRBOT');
insert into stats.temp values ('Nokia-WAPToolkit');
insert into stats.temp values ('AnsearchBot');
insert into stats.temp values ('VSynCrawler');
insert into stats.temp values ('envolk');
insert into stats.temp values ('favorstarbot');
insert into stats.temp values ('webwombat');
insert into stats.temp values ('wectar');
insert into stats.temp values ('GeoBot');
insert into stats.temp values ('wbdbot');
insert into stats.temp values ('SpiritWalker');
insert into stats.temp values ('FDSE robot');
insert into stats.temp values ('FlickBot');
insert into stats.temp values ('planethosting.com');
insert into stats.temp values ('LinkWalker');
insert into stats.temp values ('SpiderKU');
insert into stats.temp values ('InternetLink');
insert into stats.temp values ('OnetSzukaj');
insert into stats.temp values ('webyield');
insert into stats.temp values ('Green Research');
insert into stats.temp values ('PhpDig');
insert into stats.temp values ('GregBot');
insert into stats.temp values ('tuezilla');
insert into stats.temp values ('FasyBug');
insert into stats.temp values ('FastBug');
insert into stats.temp values ('Xenu');
insert into stats.temp values ('NutchCVS');
insert into stats.temp values ('Big B');
insert into stats.temp values ('Links SQL');
insert into stats.temp values ('Html Link');
insert into stats.temp values ('FunnelBack');
insert into stats.temp values ('UIowaCrawler');
insert into stats.temp values ('TsWebBot');
insert into stats.temp values ('iaskspider');
insert into stats.temp values ('NimbleCrawler');
insert into stats.temp values ('battlebot');
insert into stats.temp values ('australian1.com');
insert into stats.temp values ('lwp-trivial');
insert into stats.temp values ('lwp-request');
insert into stats.temp values ('LWP::Simple');
insert into stats.temp values ('snap.com');
insert into stats.temp values ('Infoseek');
insert into stats.temp values ('MouseBOT');
insert into stats.temp values ('Microsoft-ATL-Native');
insert into stats.temp values ('parabot');
insert into stats.temp values ('NetAnts');
insert into stats.temp values ('Teradex_Crawler');
insert into stats.temp values ('Turnitin');
insert into stats.temp values ('findlinks');
insert into stats.temp values ('sherlock_spider');
insert into stats.temp values ('timboBot');
insert into stats.temp values ('VoilaBot');
insert into stats.temp values ('ciml.co.uk');
insert into stats.temp values ('JumbleBot');
insert into stats.temp values ('void-bot');
insert into stats.temp values ('Httpcheck');
insert into stats.temp values ('TulipChain');
insert into stats.temp values ('kuloko-bot');
insert into stats.temp values ('pandora');
insert into stats.temp values ('Search Agent');
insert into stats.temp values ('Netcraft');
insert into stats.temp values ('JoBo/');
insert into stats.temp values ('EmailSiphon');
insert into stats.temp values ('Custo 2.0');
insert into stats.temp values ('EmailSmartz');
insert into stats.temp values ('libwww');
insert into stats.temp values ('WebFilter');
insert into stats.temp values ('RPT-HTTP');
insert into stats.temp values ('BurstFind');
insert into stats.temp values ('DNSGroup');
insert into stats.temp values ('Crawl_App');
insert into stats.temp values ('Gulper Web Bot');
insert into stats.temp values ('DTAAgent');
insert into stats.temp values ('NetNose');
insert into stats.temp values ('Vivante Link');
insert into stats.temp values ('AvantGo');
insert into stats.temp values ('hl_ftien_spider');
insert into stats.temp values ('Art-Online');
insert into stats.temp values ('FusionBot');
insert into stats.temp values ('Zao/');
insert into stats.temp values ('zerxbot');
insert into stats.temp values ('SuperBot');
insert into stats.temp values ('Webinator');
insert into stats.temp values ('WebRACE');
insert into stats.temp values ('voyager/');
insert into stats.temp values ('k2spider');
insert into stats.temp values ('MetaGer-Link');
insert into stats.temp values ('NuSearch');
insert into stats.temp values ('GirafaBot');
insert into stats.temp values ('HooWWWer');
insert into stats.temp values ('TranSGeniKBot');
insert into stats.temp values ('Linkbot');
insert into stats.temp values ('Cerberian');
insert into stats.temp values ('LARBIN');
insert into stats.temp values ('Larbin');
insert into stats.temp values ('W3C-');
insert into stats.temp values ('antibot');
insert into stats.temp values ('BrailleBo');
insert into stats.temp values ('webbot');
insert into stats.temp values ('SYCLIK');
insert into stats.temp values ('BorderManager');
insert into stats.temp values ('SurveyBot');
insert into stats.temp values ('sdcresearch');
insert into stats.temp values ('Teradex');
insert into stats.temp values ('iSEEKbot');
insert into stats.temp values ('Quantcastbot');
insert into stats.temp values ('oso.octopodus');
insert into stats.temp values ('MileNSbot');
insert into stats.temp values ('Snappy');
insert into stats.temp values ('exooba');
insert into stats.temp values ('FeedHub');
insert into stats.temp values ('iFeed');
insert into stats.temp values ('Google/');

