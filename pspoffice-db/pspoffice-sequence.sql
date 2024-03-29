update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `person_string_culture`)+1 where sequence_name = 'person_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `person`)+1 where sequence_name = 'person';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `registration_string_culture`)+1 where sequence_name = 'registration_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `registration`)+1 where sequence_name = 'registration';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `owner_relationship_string_culture`)+1 where sequence_name = 'owner_relationship_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `owner_relationship`)+1 where sequence_name = 'owner_relationship';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `apartment_card_string_culture`)+1 where sequence_name = 'apartment_card_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `apartment_card`)+1 where sequence_name = 'apartment_card';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `ownership_form_string_culture`)+1 where sequence_name = 'ownership_form_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `ownership_form`)+1 where sequence_name = 'ownership_form';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `registration_type_string_culture`)+1 where sequence_name = 'registration_type_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `registration_type`)+1 where sequence_name = 'registration_type';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `document_type_string_culture`)+1 where sequence_name = 'document_type_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `document_type`)+1 where sequence_name = 'document_type';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `document_string_culture`)+1 where sequence_name = 'document_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `document`)+1 where sequence_name = 'document';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `military_service_relation`)+1 where sequence_name = 'military_service_relation';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `military_service_relation_string_culture`)+1 where sequence_name = 'military_service_relation_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `departure_reason`)+1 where sequence_name = 'departure_reason';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `departure_reason_string_culture`)+1 where sequence_name = 'departure_reason_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `housing_rights`)+1 where sequence_name = 'housing_rights';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `housing_rights_string_culture`)+1 where sequence_name = 'housing_rights_string_culture';
