update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `person_string_culture`)+1 where sequence_name = 'person_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `person`)+1 where sequence_name = 'person';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `registration_string_culture`)+1 where sequence_name = 'registration_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `registration`)+1 where sequence_name = 'registration';