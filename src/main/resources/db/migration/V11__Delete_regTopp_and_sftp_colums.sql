alter table chouette_info
drop column regtopp_calendar_strategy,
drop column regtopp_coordinate_projection,
drop column regtopp_version;

alter table provider
drop column  sftp_account;