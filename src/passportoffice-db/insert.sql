insert into `locales`(`locale`, `system`) values ('ru', 1);
insert into `locales`(`locale`, `system`) values ('en', 0);

insert into `sequence` (`sequence_name`, `sequence_value`) values
('string_culture',1),
('apartment',1), ('apartment_string_culture',1), ('building',1), ('building_string_culture',1),
('country',1), ('country_string_culture',1), ('district',1), ('district_string_culture',1),
('city',1), ('city_string_culture',1), ('region',1), ('region_string_culture',1),
('room',1), ('room_string_culture',1), ('street',1), ('street_string_culture',1),
('person',1), ('person_string_culture',1);

insert into `string_culture`(`id`, `locale`, `value`) values (1, 'ru', 'Квартира'), (1, 'en', 'Apartment');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (1, 'apartment', 1, '');
insert into `string_culture`(`id`, `locale`, `value`) values (2, 'ru', 'Наименование квартиры'), (2, 'en', 'Apartment name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (1, 1, 1, 2, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (1, 1, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values (3, 'ru', 'Комната'), (3, 'en', 'Room');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (2, 'room', 3, '');
insert into `string_culture`(`id`, `locale`, `value`) values (4, 'ru', 'Наименование комнаты'), (4, 'en', 'Room name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2, 2, 1, 4, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2, 2, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values (5, 'ru', 'Улица'), (5, 'en', 'Street');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (3, 'street', 5, '');
insert into `string_culture`(`id`, `locale`, `value`) values (6, 'ru', 'Наименование улицы'), (6, 'en', 'Street name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (3, 3, 1, 6, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (3, 3, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values
(7, 'ru', 'улица'), (7, 'en', 'street'),
(8, 'ru', 'проспект'), (8, 'en', 'avenue'),
(9, 'ru', 'переулок'), (9, 'en', 'alley');
insert into `entity_type` (`id`, `entity_id`, `entity_type_name_id`) values
(1, 3, 7), (2, 3, 8), (3, 3, 9);
insert into `string_culture`(`id`, `locale`, `value`) values (10, 'ru', 'Населенный пункт'), (10, 'en', 'City');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (4, 'city', 10, '');
insert into `string_culture`(`id`, `locale`, `value`) values (11, 'ru', 'Наименование населенного пункта'), (11, 'en', 'City name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (4, 4, 1, 11, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (4, 4, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values
(12, 'ru', 'город'), (12, 'en', 'city'),
(13, 'ru', 'деревня'), (13, 'en', 'village');
insert into `entity_type`(`id`, `entity_id`, `entity_type_name_id`) values (4, 4, 12), (5, 4, 13);
insert into `string_culture`(`id`, `locale`, `value`) values (14, 'ru', 'Дом'), (14, 'en', 'Building');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (5, 'building', 14, '');
insert into `string_culture`(`id`, `locale`, `value`) values (15, 'ru', 'Номер дома'), (15, 'en', 'Building number');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (5, 5, 1, 15, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (16, 'ru', 'Корпус'), (16, 'en', 'Building corps.');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (6, 5, 0, 16, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (17, 'ru', 'Строение'), (17, 'en', 'Building structure');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (7, 5, 0, 17, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (18, 'ru', 'Улица'), (18, 'en', 'Street');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (8, 5, 0, 18, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (5, 5, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (6, 6, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (7, 7, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (8, 8, 'street');
insert into `string_culture`(`id`, `locale`, `value`) values (19, 'ru', 'Район'), (19, 'en', 'District');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (6, 'district', 19, '');
insert into `string_culture`(`id`, `locale`, `value`) values (20, 'ru', 'Наименование района'), (120, 'en', 'District name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (9, 6, 1, 20, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (9, 9, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values (21, 'ru', 'Регион'), (21, 'en', 'Region');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (7, 'region', 21, '');
insert into `string_culture`(`id`, `locale`, `value`) values (22, 'ru', 'Наименование региона'), (22, 'en', 'Region name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (10, 7, 1, 22, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (10, 10, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values (23, 'ru', 'Страна'), (23, 'en', 'Country');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (8, 'country', 23, '');
insert into `string_culture`(`id`, `locale`, `value`) values (24, 'ru', 'Наименование страны'), (24, 'en', 'Country name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (11, 8, 1, 24, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (11, 11, 'string');
insert into `string_culture`(`id`, `locale`, `value`) values (25, 'ru', 'Персона'), (25, 'en', 'Person');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (9, 'person', 25, '');
insert into `string_culture`(`id`, `locale`, `value`) values (26, 'ru', 'Имя'), (26, 'en', 'Name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (12, 9, 1, 26, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (27, 'ru', 'Отчество'), (27, 'en', 'Middle name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (13, 9, 1, 27, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (28, 'ru', 'Фамилия'), (28, 'en', 'Last name');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (14, 9, 1, 28, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (29, 'ru', 'Дата рождения'), (29, 'en', 'Birth date');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (15, 9, 1, 29, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (30, 'ru', 'Пол'), (30, 'en', 'Sex');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (16, 9, 1, 30, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (31, 'ru', 'Номер паспорта'), (31, 'en', 'Passport number');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (17, 9, 1, 31, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (32, 'ru', 'Серия паспорта'), (32, 'en', 'Passport serial number');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (18, 9, 1, 32, 1);
insert into `string_culture`(`id`, `locale`, `value`) values (33, 'ru', 'Адрес'), (33, 'en', 'Address');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (19, 9, 1, 33, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (12, 12, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (13, 13, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (14, 14, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (15, 15, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (16, 16, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (17, 17, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (18, 18, 'string');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (19, 19, 'room');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (20, 19, 'apartment');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (21, 19, 'building');

update `sequence` set `sequence_value` = 34 where `sequence_name` = 'string_culture';