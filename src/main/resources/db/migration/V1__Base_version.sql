--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: administrative_zone; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE administrative_zone (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    administrative_zone_type character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    source character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL,
    polygon_id bigint
);


ALTER TABLE administrative_zone OWNER TO baba;

--
-- Name: chouette_info; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE chouette_info (
    id bigint NOT NULL,
    allow_create_missing_stop_place boolean NOT NULL,
    data_format character varying(255),
    enable_clean_import boolean NOT NULL,
    enable_stop_place_id_mapping boolean NOT NULL,
    enable_validation boolean NOT NULL,
    migrate_data_to_provider bigint,
    organisation character varying(255),
    referential character varying(255),
    regtopp_calendar_strategy character varying(255),
    regtopp_coordinate_projection character varying(255),
    regtopp_version character varying(255),
    cuser character varying(255),
    xmlns character varying(255),
    xmlnsurl character varying(255)
);


ALTER TABLE chouette_info OWNER TO baba;

--
-- Name: code_space; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE code_space (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    xmlns character varying(255) NOT NULL,
    xmlns_url character varying(255) NOT NULL
);


ALTER TABLE code_space OWNER TO baba;

--
-- Name: contact_details; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE contact_details (
    pk bigint NOT NULL,
    email character varying(255) NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    phone character varying(255)
);


ALTER TABLE contact_details OWNER TO baba;

--
-- Name: entity_classification; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE entity_classification (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL,
    entity_type_pk bigint NOT NULL
);


ALTER TABLE entity_classification OWNER TO baba;

--
-- Name: entity_classification_assignment; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE entity_classification_assignment (
    pk bigint NOT NULL,
    allow boolean NOT NULL,
    entity_classification_pk bigint NOT NULL,
    responsibility_role_assignment_pk bigint
);


ALTER TABLE entity_classification_assignment OWNER TO baba;

--
-- Name: entity_type; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE entity_type (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL
);


ALTER TABLE entity_type OWNER TO baba;

--
-- Name: event_filter; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE event_filter (
    dtype character varying(31) NOT NULL,
    pk bigint NOT NULL,
    job_domain character varying(255),
    organisation_pk bigint
);


ALTER TABLE event_filter OWNER TO baba;

--
-- Name: event_filter_administrative_zones; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE event_filter_administrative_zones (
    crud_event_filter_pk bigint NOT NULL,
    administrative_zones_pk bigint NOT NULL
);


ALTER TABLE event_filter_administrative_zones OWNER TO baba;

--
-- Name: event_filter_entity_classifications; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE event_filter_entity_classifications (
    crud_event_filter_pk bigint NOT NULL,
    entity_classifications_pk bigint NOT NULL
);


ALTER TABLE event_filter_entity_classifications OWNER TO baba;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: baba
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO baba;

--
-- Name: job_event_filter_actions; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE job_event_filter_actions (
    job_event_filter_pk bigint NOT NULL,
    actions character varying(255)
);


ALTER TABLE job_event_filter_actions OWNER TO baba;

--
-- Name: job_event_filter_states; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE job_event_filter_states (
    job_event_filter_pk bigint NOT NULL,
    states integer
);


ALTER TABLE job_event_filter_states OWNER TO baba;

--
-- Name: notification_configuration; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE notification_configuration (
    pk bigint NOT NULL,
    enabled boolean NOT NULL,
    notification_type character varying(255) NOT NULL,
    event_filter_pk bigint NOT NULL
);


ALTER TABLE notification_configuration OWNER TO baba;

--
-- Name: organisation; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE organisation (
    dtype character varying(31) NOT NULL,
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    company_number bigint,
    name character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL
);


ALTER TABLE organisation OWNER TO baba;

--
-- Name: organisation_part; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE organisation_part (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL
);


ALTER TABLE organisation_part OWNER TO baba;

--
-- Name: organisation_part_administrative_zones; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE organisation_part_administrative_zones (
    organisation_part_pk bigint NOT NULL,
    administrative_zones_pk bigint NOT NULL
);


ALTER TABLE organisation_part_administrative_zones OWNER TO baba;

--
-- Name: organisation_parts; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE organisation_parts (
    organisation_pk bigint NOT NULL,
    parts_pk bigint NOT NULL
);


ALTER TABLE organisation_parts OWNER TO baba;

--
-- Name: persistable_polygon; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE persistable_polygon (
    id bigint NOT NULL,
    polygon geometry NOT NULL
);


ALTER TABLE persistable_polygon OWNER TO baba;

--
-- Name: provider; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE provider (
    id bigint NOT NULL,
    name character varying(255),
    sftp_account character varying(255),
    chouette_info_id bigint
);


ALTER TABLE provider OWNER TO baba;

--
-- Name: responsibility_role_assignment; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE responsibility_role_assignment (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL,
    responsible_area_pk bigint,
    responsible_organisation_pk bigint NOT NULL,
    type_of_responsibility_role_pk bigint NOT NULL
);


ALTER TABLE responsibility_role_assignment OWNER TO baba;

--
-- Name: responsibility_set; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE responsibility_set (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    code_space_pk bigint NOT NULL
);


ALTER TABLE responsibility_set OWNER TO baba;

--
-- Name: responsibility_set_roles; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE responsibility_set_roles (
    responsibility_set_pk bigint NOT NULL,
    roles_pk bigint NOT NULL
);


ALTER TABLE responsibility_set_roles OWNER TO baba;

--
-- Name: role; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE role (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE role OWNER TO baba;

--
-- Name: user_account; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE user_account (
    pk bigint NOT NULL,
    entity_version bigint NOT NULL,
    lock_version bigint NOT NULL,
    private_code character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    contact_details_pk bigint NOT NULL,
    organisation_pk bigint NOT NULL
);


ALTER TABLE user_account OWNER TO baba;

--
-- Name: user_account_notification_configurations; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE user_account_notification_configurations (
    user_pk bigint NOT NULL,
    notification_configurations_pk bigint NOT NULL
);


ALTER TABLE user_account_notification_configurations OWNER TO baba;

--
-- Name: user_account_responsibility_sets; Type: TABLE; Schema: public; Owner: baba
--

CREATE TABLE user_account_responsibility_sets (
    user_pk bigint NOT NULL,
    responsibility_sets_pk bigint NOT NULL
);


ALTER TABLE user_account_responsibility_sets OWNER TO baba;

--
-- Name: adm_zone_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY administrative_zone
    ADD CONSTRAINT adm_zone_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: administrative_zone_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY administrative_zone
    ADD CONSTRAINT administrative_zone_pkey PRIMARY KEY (pk);


--
-- Name: chouette_info_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY chouette_info
    ADD CONSTRAINT chouette_info_pkey PRIMARY KEY (id);


--
-- Name: code_space_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY code_space
    ADD CONSTRAINT code_space_pkey PRIMARY KEY (pk);


--
-- Name: code_space_unique_private_code; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY code_space
    ADD CONSTRAINT code_space_unique_private_code UNIQUE (private_code, entity_version);


--
-- Name: code_space_unique_xmlns; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY code_space
    ADD CONSTRAINT code_space_unique_xmlns UNIQUE (xmlns, entity_version);


--
-- Name: contact_details_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY contact_details
    ADD CONSTRAINT contact_details_pkey PRIMARY KEY (pk);


--
-- Name: entity_classification_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification_assignment
    ADD CONSTRAINT entity_classification_assignment_pkey PRIMARY KEY (pk);


--
-- Name: entity_classification_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification
    ADD CONSTRAINT entity_classification_pkey PRIMARY KEY (pk);


--
-- Name: entity_classification_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification
    ADD CONSTRAINT entity_classification_unique_id UNIQUE (code_space_pk, private_code, entity_version, entity_type_pk);


--
-- Name: entity_type_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_type
    ADD CONSTRAINT entity_type_pkey PRIMARY KEY (pk);


--
-- Name: entity_type_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_type
    ADD CONSTRAINT entity_type_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: event_filter_administrative_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_administrative_zones
    ADD CONSTRAINT event_filter_administrative_zones_pkey PRIMARY KEY (crud_event_filter_pk, administrative_zones_pk);


--
-- Name: event_filter_entity_classifications_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_entity_classifications
    ADD CONSTRAINT event_filter_entity_classifications_pkey PRIMARY KEY (crud_event_filter_pk, entity_classifications_pk);


--
-- Name: event_filter_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter
    ADD CONSTRAINT event_filter_pkey PRIMARY KEY (pk);


--
-- Name: notification_configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY notification_configuration
    ADD CONSTRAINT notification_configuration_pkey PRIMARY KEY (pk);


--
-- Name: org_part_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part
    ADD CONSTRAINT org_part_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: org_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation
    ADD CONSTRAINT org_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: organisation_part_administrative_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part_administrative_zones
    ADD CONSTRAINT organisation_part_administrative_zones_pkey PRIMARY KEY (organisation_part_pk, administrative_zones_pk);


--
-- Name: organisation_part_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part
    ADD CONSTRAINT organisation_part_pkey PRIMARY KEY (pk);


--
-- Name: organisation_parts_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_parts
    ADD CONSTRAINT organisation_parts_pkey PRIMARY KEY (organisation_pk, parts_pk);


--
-- Name: organisation_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation
    ADD CONSTRAINT organisation_pkey PRIMARY KEY (pk);


--
-- Name: persistable_polygon_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY persistable_polygon
    ADD CONSTRAINT persistable_polygon_pkey PRIMARY KEY (id);


--
-- Name: provider_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY provider
    ADD CONSTRAINT provider_pkey PRIMARY KEY (id);


--
-- Name: responsibility_role_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT responsibility_role_assignment_pkey PRIMARY KEY (pk);


--
-- Name: responsibility_set_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set
    ADD CONSTRAINT responsibility_set_pkey PRIMARY KEY (pk);


--
-- Name: responsibility_set_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set_roles
    ADD CONSTRAINT responsibility_set_roles_pkey PRIMARY KEY (responsibility_set_pk, roles_pk);


--
-- Name: responsibility_set_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set
    ADD CONSTRAINT responsibility_set_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (pk);


--
-- Name: role_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_unique_id UNIQUE (private_code, entity_version);


--
-- Name: rsp_role_assignment_unique_id; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT rsp_role_assignment_unique_id UNIQUE (code_space_pk, private_code, entity_version);


--
-- Name: uk_7ghge2jt2h0cgdv60ucgppy89; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY code_space
    ADD CONSTRAINT uk_7ghge2jt2h0cgdv60ucgppy89 UNIQUE (xmlns);


--
-- Name: uk_hfyy8y4pw0760jgqfb54iilof; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_notification_configurations
    ADD CONSTRAINT uk_hfyy8y4pw0760jgqfb54iilof UNIQUE (notification_configurations_pk);


--
-- Name: uk_pmvr0yuncdmu2aspqnumrx51u; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY code_space
    ADD CONSTRAINT uk_pmvr0yuncdmu2aspqnumrx51u UNIQUE (xmlns_url);


--
-- Name: uk_q300n8sbdadwb5sjq5m6wqlwb; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_parts
    ADD CONSTRAINT uk_q300n8sbdadwb5sjq5m6wqlwb UNIQUE (parts_pk);


--
-- Name: uk_soty89jj19k8nq5shegpn91pg; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set_roles
    ADD CONSTRAINT uk_soty89jj19k8nq5shegpn91pg UNIQUE (roles_pk);


--
-- Name: user_account_notification_configurations_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_notification_configurations
    ADD CONSTRAINT user_account_notification_configurations_pkey PRIMARY KEY (user_pk, notification_configurations_pk);


--
-- Name: user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_pkey PRIMARY KEY (pk);


--
-- Name: user_account_responsibility_sets_pkey; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_responsibility_sets
    ADD CONSTRAINT user_account_responsibility_sets_pkey PRIMARY KEY (user_pk, responsibility_sets_pk);


--
-- Name: user_unique_username; Type: CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_unique_username UNIQUE (private_code, entity_version);


--
-- Name: fk1crbnp6byqvgwuvix51vulhiv; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part
    ADD CONSTRAINT fk1crbnp6byqvgwuvix51vulhiv FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fk2cm92x84r8589kxnvuu0a3msv; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_entity_classifications
    ADD CONSTRAINT fk2cm92x84r8589kxnvuu0a3msv FOREIGN KEY (entity_classifications_pk) REFERENCES entity_classification(pk);


--
-- Name: fk2faooi0p0do71xrfvr72f5kur; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification
    ADD CONSTRAINT fk2faooi0p0do71xrfvr72f5kur FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fk4b1kehc9o1bii4lglph69qrs; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation
    ADD CONSTRAINT fk4b1kehc9o1bii4lglph69qrs FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fk58auklt1uuglm5kovv9mq6rvl; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_responsibility_sets
    ADD CONSTRAINT fk58auklt1uuglm5kovv9mq6rvl FOREIGN KEY (user_pk) REFERENCES user_account(pk);


--
-- Name: fk5c22irvx8ve5t3dtdekxx1l29; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification
    ADD CONSTRAINT fk5c22irvx8ve5t3dtdekxx1l29 FOREIGN KEY (entity_type_pk) REFERENCES entity_type(pk);


--
-- Name: fk5cpp08w6qsi0c6t3plfsk1dio; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set_roles
    ADD CONSTRAINT fk5cpp08w6qsi0c6t3plfsk1dio FOREIGN KEY (responsibility_set_pk) REFERENCES responsibility_set(pk);


--
-- Name: fk6456sur060j13di09f8osy1h; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification_assignment
    ADD CONSTRAINT fk6456sur060j13di09f8osy1h FOREIGN KEY (responsibility_role_assignment_pk) REFERENCES responsibility_role_assignment(pk);


--
-- Name: fk7d98gec1egtemddacc96vdhkb; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_administrative_zones
    ADD CONSTRAINT fk7d98gec1egtemddacc96vdhkb FOREIGN KEY (administrative_zones_pk) REFERENCES administrative_zone(pk);


--
-- Name: fk7pb4lt1w5g9bugw757gouvxu1; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY job_event_filter_actions
    ADD CONSTRAINT fk7pb4lt1w5g9bugw757gouvxu1 FOREIGN KEY (job_event_filter_pk) REFERENCES event_filter(pk);


--
-- Name: fk8v7h1esa9wfkpkehueuloomvd; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_notification_configurations
    ADD CONSTRAINT fk8v7h1esa9wfkpkehueuloomvd FOREIGN KEY (user_pk) REFERENCES user_account(pk);


--
-- Name: fk90vg0y9xkuwtrsslwe7x7csx6; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set
    ADD CONSTRAINT fk90vg0y9xkuwtrsslwe7x7csx6 FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fk9gl34wi36bjxeeeo5k322r8gu; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_entity_classifications
    ADD CONSTRAINT fk9gl34wi36bjxeeeo5k322r8gu FOREIGN KEY (crud_event_filter_pk) REFERENCES event_filter(pk);


--
-- Name: fk9kr6b5tdbw1o1cder2dvkllr3; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_type
    ADD CONSTRAINT fk9kr6b5tdbw1o1cder2dvkllr3 FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fkbb8eph5sijrjw5nxw47oetdit; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_parts
    ADD CONSTRAINT fkbb8eph5sijrjw5nxw47oetdit FOREIGN KEY (organisation_pk) REFERENCES organisation(pk);


--
-- Name: fkbpwulfqc3dfd01k1koxwt7qk; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_notification_configurations
    ADD CONSTRAINT fkbpwulfqc3dfd01k1koxwt7qk FOREIGN KEY (notification_configurations_pk) REFERENCES notification_configuration(pk);


--
-- Name: fkc5iwh1piq3tlmisqjihcsqgja; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter_administrative_zones
    ADD CONSTRAINT fkc5iwh1piq3tlmisqjihcsqgja FOREIGN KEY (crud_event_filter_pk) REFERENCES event_filter(pk);


--
-- Name: fkeg34y1779x0gj2i2eqa0g7vce; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account_responsibility_sets
    ADD CONSTRAINT fkeg34y1779x0gj2i2eqa0g7vce FOREIGN KEY (responsibility_sets_pk) REFERENCES responsibility_set(pk);


--
-- Name: fkg408plscwcfawhs1fq4pj7o7q; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT fkg408plscwcfawhs1fq4pj7o7q FOREIGN KEY (type_of_responsibility_role_pk) REFERENCES role(pk);


--
-- Name: fkhfbarb4wn0rbutp24pce4echr; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT fkhfbarb4wn0rbutp24pce4echr FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fkhmabvvk54erja1ophtnv2d86w; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part_administrative_zones
    ADD CONSTRAINT fkhmabvvk54erja1ophtnv2d86w FOREIGN KEY (administrative_zones_pk) REFERENCES administrative_zone(pk);


--
-- Name: fkic8d6ylholv5o3o2ys0pr1s6l; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT fkic8d6ylholv5o3o2ys0pr1s6l FOREIGN KEY (responsible_area_pk) REFERENCES administrative_zone(pk);


--
-- Name: fkivsbnsn2cn2avfbc4hmcmq365; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY administrative_zone
    ADD CONSTRAINT fkivsbnsn2cn2avfbc4hmcmq365 FOREIGN KEY (polygon_id) REFERENCES persistable_polygon(id);


--
-- Name: fkkak0ou3e4n9uo6ascfs0c0rrx; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_role_assignment
    ADD CONSTRAINT fkkak0ou3e4n9uo6ascfs0c0rrx FOREIGN KEY (responsible_organisation_pk) REFERENCES organisation(pk);


--
-- Name: fkkgekytn93vjnks3fmhapch1os; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY entity_classification_assignment
    ADD CONSTRAINT fkkgekytn93vjnks3fmhapch1os FOREIGN KEY (entity_classification_pk) REFERENCES entity_classification(pk);


--
-- Name: fkmc0toe1cy3lvvf45sajv8uh6h; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_part_administrative_zones
    ADD CONSTRAINT fkmc0toe1cy3lvvf45sajv8uh6h FOREIGN KEY (organisation_part_pk) REFERENCES organisation_part(pk);


--
-- Name: fkmfmmy1ag4cn0n5ddhajy13oh6; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY provider
    ADD CONSTRAINT fkmfmmy1ag4cn0n5ddhajy13oh6 FOREIGN KEY (chouette_info_id) REFERENCES chouette_info(id);


--
-- Name: fkmvwm4r2314os2qdulyv9761pm; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY responsibility_set_roles
    ADD CONSTRAINT fkmvwm4r2314os2qdulyv9761pm FOREIGN KEY (roles_pk) REFERENCES responsibility_role_assignment(pk);


--
-- Name: fknapen08vwyxkm6mqkt85jj3a0; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY organisation_parts
    ADD CONSTRAINT fknapen08vwyxkm6mqkt85jj3a0 FOREIGN KEY (parts_pk) REFERENCES organisation_part(pk);


--
-- Name: fkp17fck08v09vrkmuu5jhs1upk; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY administrative_zone
    ADD CONSTRAINT fkp17fck08v09vrkmuu5jhs1upk FOREIGN KEY (code_space_pk) REFERENCES code_space(pk);


--
-- Name: fkqbrkv0h9ph1csvr4cn0pqg346; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY notification_configuration
    ADD CONSTRAINT fkqbrkv0h9ph1csvr4cn0pqg346 FOREIGN KEY (event_filter_pk) REFERENCES event_filter(pk);


--
-- Name: fkqf510c7maxtb90v8p6riu4hrm; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fkqf510c7maxtb90v8p6riu4hrm FOREIGN KEY (contact_details_pk) REFERENCES contact_details(pk);


--
-- Name: fkqxbb6wgxuhyp99c3r2x46tofq; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY event_filter
    ADD CONSTRAINT fkqxbb6wgxuhyp99c3r2x46tofq FOREIGN KEY (organisation_pk) REFERENCES organisation(pk);


--
-- Name: fkr3ajawygx32bn8g133anveqeh; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY job_event_filter_states
    ADD CONSTRAINT fkr3ajawygx32bn8g133anveqeh FOREIGN KEY (job_event_filter_pk) REFERENCES event_filter(pk);


--
-- Name: fks4pv0oa5l287g8boef2re8s08; Type: FK CONSTRAINT; Schema: public; Owner: baba
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fks4pv0oa5l287g8boef2re8s08 FOREIGN KEY (organisation_pk) REFERENCES organisation(pk);


--
-- PostgreSQL database dump complete
--

