-- Owner relationship --
INSERT INTO `owner_relationship`(`object_id`) VALUES (4),(5),(6),(7),(8);
INSERT INTO `owner_relationship_string_culture`(`id`, `locale_id`, `value`) VALUES
(4, 1, UPPER('ответственный квартиросьемщик')), (4, 2,UPPER('УПОВНОВАЖЕНИЙ ВЛАСНИК')),
(5, 1, UPPER('гость')), (5, 2, UPPER('IНША ОСОБА')),
(6, 1, UPPER('муж')), (6, 2, UPPER('ЧОЛОВIК')),
(7, 1, UPPER('жена')), (7, 2, UPPER('ДРУЖИНА')),
(8, 1, UPPER('дальний родственник')), (8, 2, UPPER('IНШИЙ РОДИЧ'));
INSERT INTO `owner_relationship_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,4,2200,4,2200),(1,5,2200,5,2200),(1,6,2200,6,2200),(1,7,2200,7,2200),(1,8,2200,8,2200);

-- Forms of ownerships -- 
INSERT INTO `ownership_form`(`object_id`) VALUES (1),(2),(3),(4),(5),(6),(7),(8);
INSERT INTO `ownership_form_string_culture`(`id`, `locale_id`, `value`) VALUES (1, 1, UPPER('мiсцевих Рад')), (1, 2,UPPER('мiсцевих Рад')),
(2, 1, UPPER('кооперативна')), (2, 2, UPPER('кооперативна')), (3, 1, UPPER('вiдомча')), (3, 2, UPPER('вiдомча')),
(4, 1, UPPER('громадська')), (4, 2, UPPER('громадська')), (5, 1, UPPER('приватна')), (5, 2, UPPER('приватна')),
(6, 1, UPPER('приватизована')), (6, 2, UPPER('приватизована')), (7, 1, UPPER('викуплена')), (7, 2, UPPER('викуплена')),
(8, 1, UPPER('службова')), (8, 2, UPPER('службова'));
INSERT INTO `ownership_form_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,2500,1,2500),(1,2,2500,2,2500),(1,3,2500,3,2500),(1,4,2500,4,2500),(1,5,2500,5,2500),(1,6,2500,6,2500), (1,7,2500,7,2500),
 (1,8,2500,8,2500);


--insert into config(`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', 'D:\\Artem\\Projects\\org.complitex\\storage\\import3');
--insert into config(`name`, `value`) values ('DEFAULT_IMPORT_FILE_DIR', 'D:\\Artem\\Projects\\org.complitex\\pspoffice\\docs\\PSW_Rada');
--insert into config(`name`, `value`) values ('DEFAULT_IMPORT_FILE_ERRORS_DIR', 'D:\\Artem\\Projects\\org.complitex\\pspoffice\\docs\\PSW_Rada\\errors');