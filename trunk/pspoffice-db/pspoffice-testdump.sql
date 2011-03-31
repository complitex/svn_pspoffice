insert into last_name(id, name) values (1,'Матвеев');
insert into first_name(id, name) values (1,'Матвей');
insert into middle_name(id, name) values (1,'Матвеевич');

insert into registration_string_culture(id, locale_id, value) values (1,1,'Владелец'), (2,1,'Солнечная система, планета Земля');
insert into registration(object_id) values (1);
insert into registration_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,1,2101,2,2103),(1,1,2100,1,2100),(1,1,2103,1,2111);

insert into person_string_culture(id, locale_id, value) values (1,1,'01.01.1300'), (2,1,'Планета Земля'), (3,1,'1234 12345678');
insert into person(object_id) values (1);
insert into person_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,1,2000,1,2000), (1,1,2001,1,2001), (1,1,2002,1,2002),
(1,1,2003,1,2003), (1,1,2004,2,2004), (1,1,2005,3,2005), (1,1,2006,1,2006);