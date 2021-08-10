alter table chouette_info_service_link_modes
    add constraint chouette_info_service_link_modes_pk
        primary key (chouette_info_id, transport_mode);

alter table job_event_filter_actions
    add constraint job_event_filter_actions_pk
        primary key (job_event_filter_pk);

alter table job_event_filter_states
    add constraint job_event_filter_states_pk
        primary key (job_event_filter_pk);