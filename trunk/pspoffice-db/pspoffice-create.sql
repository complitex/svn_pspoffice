-- person --

DROP TABLE IF EXISTS `person_attribute`;
DROP TABLE IF EXISTS `person`;
DROP TABLE IF EXISTS `person_string_culture`;

CREATE TABLE `person` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20) NOT NULL,
  `parent_id` BIGINT(20),
  `parent_entity_id` BIGINT(20),
  `entity_type_id` BIGINT(20),
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0,
  `external_id` BIGINT(20),
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (object_id),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_entity_type_id` (`entity_type_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_person__entity_type` FOREIGN KEY (`entity_type_id`) REFERENCES `entity_type` (`id`),
  CONSTRAINT `fk_person__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_person__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `attribute_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20) NOT NULL,
  `attribute_type_id` BIGINT(20) NOT NULL,
  `value_id` BIGINT(20),
  `value_type_id` BIGINT(20) NOT NULL,
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_date` TIMESTAMP NULL DEFAULT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_person_attribute__person` FOREIGN KEY (`object_id`) REFERENCES `person`(`object_id`),
  CONSTRAINT `fk_person_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
    REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_person_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
    REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `person_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `id` BIGINT(20) NOT NULL,
  `locale_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(1000),
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_person_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
