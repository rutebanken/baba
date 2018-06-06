
create table CHOUETTE_INFO_SERVICE_LINK_MODES (
    chouette_info_id bigint NOT NULL,
    transport_mode character varying(255) NOT NULL
);

ALTER TABLE ONLY CHOUETTE_INFO_SERVICE_LINK_MODES
    ADD CONSTRAINT chouette_info_fkey FOREIGN KEY (chouette_info_id) REFERENCES public.chouette_info(id) ON DELETE CASCADE;

ALTER TABLE CHOUETTE_INFO DROP COLUMN  generate_missing_service_links;
