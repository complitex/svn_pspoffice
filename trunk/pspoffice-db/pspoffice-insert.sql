
-- Person --
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('person',1), ('person_string_culture',1);

insert into `string_culture`(`id`, `locale_id`, `value`) values (2000, 1, 'Персона'), (2000, 2, 'Person');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (2000, 'person', 2000, '');
insert into `string_culture`(`id`, `locale_id`, `value`) values (2001, 1, 'Фамилия'), (2001, 2, 'Прізвище');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2000, 2000, 1, 2001, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2002, 1, 'Имя'), (2002, 2, 'Ім\'я');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2001, 2000, 1, 2002, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2003, 1, 'Отчество'), (2003, 2, 'По батькові');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2002, 2000, 1, 2003, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2004, 1, 'Дата рождения'), (2004, 2, 'Дата рождения');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2003, 2000, 1, 2004, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2005, 1, 'Место рождения'), (2005, 2, 'Место рождения');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2004, 2000, 1, 2005, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2006, 1, 'Пол'), (2006, 2, 'Пол');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2005, 2000, 1, 2006, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2007, 1, 'Документ удостоверяющий личность'), (2007, 2, 'Документ удостоверяющий личность');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2006, 2000, 1, 2007, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2008, 1, 'Регистрация'), (2008, 2, 'Регистрация');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2007, 2000, 1, 2008, 1);

insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2000, 2000, UPPER('last_name'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2001, 2001, UPPER('first_name'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2002, 2002, UPPER('middle_name'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2003, 2003, UPPER('date'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2004, 2004, UPPER('string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2005, 2005, UPPER('boolean'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2006, 2006, UPPER('string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2007, 2007, 'registration');

INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES ('registration',1), ('registration_string_culture',1);

insert into `string_culture`(`id`, `locale_id`, `value`) values (2100, 1, 'Регистрация'), (2100, 2, 'Регистрация');
insert into `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) values (2100, 'registration', 2100, '');
insert into `string_culture`(`id`, `locale_id`, `value`) values (2101, 1, 'Адрес'), (2101, 2, 'Адрес');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2100, 2100, 1, 2101, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2102, 1, 'Адрес прибытия'), (2102, 2, 'Адрес прибытия');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2101, 2100, 1, 2102, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2103, 1, 'Адрес убытия'), (2103, 2, 'Адрес убытия');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2102, 2100, 1, 2103, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2104, 1, 'Отношение к владельцу'), (2104, 2, 'Отношение к владельцу');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2103, 2100, 1, 2104, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2105, 1, 'Отношение к остальным проживающим'), (2105, 2, 'Отношение к остальным проживающим');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2104, 2100, 0, 2105, 1);
insert into `string_culture`(`id`, `locale_id`, `value`) values (2106, 1, 'Документ права на жилпощадь'), (2106, 2, 'Документ права на жилпощадь');
insert into `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) values (2105, 2100, 0, 2106, 1);
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2100, 2100, 'room');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2101, 2100, 'apartment');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2102, 2100, 'building');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2103, 2101, UPPER('big_string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2104, 2101, 'room');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2105, 2101, 'apartment');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2106, 2101, 'building');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2107, 2102, UPPER('big_string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2108, 2102, 'room');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2109, 2102, 'apartment');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2110, 2102, 'building');
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2111, 2103, UPPER('string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2112, 2104, UPPER('string'));
insert into `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) values (2113, 2105, UPPER('big_string'));