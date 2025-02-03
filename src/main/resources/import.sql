insert into code_space (pk, entity_version,lock_version,private_code, xmlns,xmlns_url) VALUES (1,1,1,'nsr','NSR','http://www.rutebanken.org/ns/nsr');
insert into code_space (pk, entity_version,lock_version,private_code, xmlns,xmlns_url) VALUES (2,1,1,'rut','RUT','http://www.rutebanken.org/ns/rut');
insert into code_space (pk, entity_version,lock_version,private_code, xmlns,xmlns_url) VALUES (3,1,1,'rb','RB','http://www.rutebanken.org/ns/rb');


insert into role (pk, entity_version,lock_version,private_code,name) VALUES (1,1,1,'editOrganisation','Create and edit users, roles, responsibility sets and organisations');

insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (1,1,1,1,'StopPlace','Stoppesteder');
insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (2,1,1,1,'PlaceOfInterest','Place of interest');

insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (1,1,1,1,1,'onstreetBus','Bus stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (2,1,1,1,1,'onstreetTram','Tram stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (3,1,1,2,1,'*','All placeOfInterest');

insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'OST','Østfold / Østfold Kollektivtrafikk');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'RUT','Oslo og Akershus / Ruter');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'HED','Hedmark / Hedmark-Trafikk');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'OPP','Oppland / Opplandstrafikk');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'BRA','Buskerud / Brakar');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'VKT','Vestfold / VkT');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'AKT','Agder / AkT');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'KOL','Rogaland / Kolumbus');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'SKY','Hordaland / Skyss');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'SOF', 'Sogn og Fjordane / Kringom');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'MOR','Møre og Romsdal / Fram');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'ATB','Sør-Trøndelag / AtB');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'NTR','Nord-Trøndelag');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'NOR','Nordland');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'TRO','Troms / Troms Fylkestrafikk');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'FIN','Finnmark / Snelandia');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'NSB','NSB');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'TEL','Telemark');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'NRI','Norsk ReiseInformasjon');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'NWY','NOR-WAY Bussekspress');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'AVI','Avinor');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'FLT','Flytoget');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'Test1','Test Organisasjon 1');
insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('versioned_entity_seq'),'Authority',1,1,1,'Test2','Test Organisasjon 2');

alter sequence versioned_entity_seq restart with 20000;