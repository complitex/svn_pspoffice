--insert into country_string_culture(id, locale, value) values (1, 'ru', 'Россия');
--insert into country_string_culture(id, locale, value) values (2, 'ru', 'Украина');
--insert into country(country_id, name_id, start_date) values (1, 1, '2010-01-15 17:59:09');
--insert into country(country_id, name_id, start_date) values (2, 2, '2010-02-15 17:59:09');
--update sequence set sequence_value = 3 where sequence_name = 'country_string_culture';
--update sequence set sequence_value = 3 where sequence_name = 'country';
--
--insert into region_string_culture(id, locale, value) values (1, 'ru', 'Новосибирская обл.');
--insert into region_string_culture(id, locale, value) values (2, 'ru', 'Московская обл.');
--insert into region(region_id, name_id, start_date, parent_id, parent_type) values (1, 1, '2010-01-15 17:59:09', 1, 'COUNTRY');
--insert into region(region_id, name_id, start_date, parent_id, parent_type) values (2, 2, '2010-02-15 17:59:09', 1, 'COUNTRY');
--update sequence set sequence_value = 3 where sequence_name = 'region_string_culture';
--update sequence set sequence_value = 3 where sequence_name = 'region';
--
--insert into city_string_culture(id, locale, value) values (1, 'ru', 'Новосибирск');
--insert into city_string_culture(id, locale, value) values (2, 'ru', 'Москва');
--insert into city(city_id, name_id, start_date, city_type_id, parent_id, parent_type) values (1, 1, '2010-01-15 17:59:09', 1, 1, 'REGION');
--insert into city(city_id, name_id, start_date, city_type_id, parent_id, parent_type) values (2, 2, '2010-02-15 17:59:09', 1, 1, 'COUNTRY');
--update sequence set sequence_value = 3 where sequence_name = 'city_string_culture';
--update sequence set sequence_value = 3 where sequence_name = 'city';
--
--insert into street_string_culture(id, locale, value) values (1, 'ru', 'Терешковой');
--insert into street_string_culture(id, locale, value) values (2, 'ru', 'Ленина');
--insert into street(street_id, name_id, start_date, street_type_id, parent_id, parent_type) values (1, 1, '2010-01-15 17:59:09', 1, 1, 'CITY');
--insert into street(street_id, name_id, start_date, street_type_id, parent_id, parent_type) values (2, 2, '2010-02-15 17:59:09', 1, 2, 'CITY');
--update sequence set sequence_value = 3 where sequence_name = 'street_string_culture';
--update sequence set sequence_value = 3 where sequence_name = 'street';
--
--insert into building(building_id, start_date) values (1, '2010-02-15 17:59:09'), (2, '2010-02-15 17:59:09'), (3, '2010-02-15 17:59:09'), (4, '2010-02-15 17:59:09');
--insert into building_string_culture(id, locale, value) values (1, 'ru', '10'), (2, 'ru', '20'), (3, 'ru', '30'), (4, 'ru', '40'),
--(5, 'ru', '2'), (6, 'ru', '8а');
--insert into building_attribute(building_id, start_date, building_number_id, building_structure_id, building_corp_id, parent_id, parent_type) values
--(1, '2010-02-15 17:59:09', 1, 5, null, 1, 'STREET'),
--(2, '2010-02-15 17:59:09', 2, null, null, 1, 'STREET'),
--(3, '2010-02-15 17:59:09', 3, null, null, 2, 'STREET'),
--(4, '2010-02-15 17:59:09', 4, null, 6, 1, 'STREET');
--update sequence set sequence_value = 7 where sequence_name = 'building_string_culture';
--update sequence set sequence_value = 5 where sequence_name = 'building';

-- Apartments
insert into apartment(object_id) values (1), (2);
insert into apartment_string_culture(id, locale, value) values (1, 'ru', '10'), (1, 'en', '10'), (2, 'ru', '20'), (2, 'en', '20');
insert into apartment_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,1,1,1,1), (1,2,1,2,1);
update sequence set sequence_value = 3 where sequence_name = 'apartment';
update sequence set sequence_value = 3 where sequence_name = 'apartment_string_culture';

-- Rooms
insert into room(object_id, parent_id, parent_entity_id) values (1,1,1), (2,1,1), (3,2,1), (4,2,1);
insert into room_string_culture(id, locale, value) values (1, 'ru', '1а'), (1, 'en', '1a'), (2, 'ru', '1б'), (2, 'en', '1b'),
(3, 'ru', '2а'), (3, 'en', '2a'), (4, 'ru', '2б'), (4, 'en', '2b');
insert into room_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,1,2,1,2), (1,2,2,2,2), (1,3,2,3,2), (1,4,2,4,2);
update sequence set sequence_value = 5 where sequence_name = 'room';
update sequence set sequence_value = 5 where sequence_name = 'room_string_culture';





