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
-- Name: orcid_authority; Type: TABLE; Schema: public; Owner: dspace; Tablespace:
--

CREATE TABLE orcid_authority (
    authority_id character varying(100),
    orcid character varying(30),
    orcid_authority_id integer NOT NULL
);


ALTER TABLE orcid_authority OWNER TO dspace;

--
-- Name: orcid_authority_seq; Type: SEQUENCE; Schema: public; Owner: dspace
--

CREATE SEQUENCE orcid_authority_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE orcid_authority_seq OWNER TO dspace;

--
-- Name: orcid_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: dspace; Tablespace:
--

ALTER TABLE ONLY orcid_authority
    ADD CONSTRAINT orcid_authority_pkey PRIMARY KEY (orcid_authority_id);


--
-- PostgreSQL database dump complete
--

