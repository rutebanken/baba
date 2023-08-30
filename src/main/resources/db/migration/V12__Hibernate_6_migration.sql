CREATE SEQUENCE entity_classification_assignment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('entity_classification_assignment_seq',  (SELECT MAX(pk) + 1 FROM entity_classification_assignment));

CREATE SEQUENCE persistable_polygon_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('persistable_polygon_seq',  (SELECT MAX(id) + 1 FROM persistable_polygon));

CREATE SEQUENCE contact_details_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('contact_details_seq',  (SELECT MAX(pk) + 1 FROM contact_details));

CREATE SEQUENCE notification_configuration_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('notification_configuration_seq',  (SELECT MAX(pk) + 1 FROM notification_configuration));


CREATE SEQUENCE chouette_info_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('chouette_info_seq',  (SELECT MAX(id) + 1 FROM chouette_info));


CREATE SEQUENCE provider_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('provider_seq',  (SELECT MAX(id) + 1 FROM provider));


CREATE SEQUENCE event_filter_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('event_filter_seq',  (SELECT MAX(pk) + 1 FROM event_filter));


CREATE SEQUENCE versioned_entity_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('versioned_entity_seq',  (SELECT MAX(pk) +1 FROM
    (SELECT pk FROM role
     UNION SELECT pk FROM user_account
     UNION SELECT pk FROM administrative_zone
     UNION SELECT pk FROM organisation
     UNION SELECT pk FROM code_space
     UNION SELECT pk FROM entity_classification
     UNION SELECT pk FROM entity_type
     UNION SELECT pk FROM organisation_part
     UNION SELECT pk FROM responsibility_role_assignment
     UNION SELECT pk FROM responsibility_set
    ) as maxPK));


