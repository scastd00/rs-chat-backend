DROP DATABASE IF EXISTS `rs_chat`;
CREATE DATABASE IF NOT EXISTS `rs_chat` CHARACTER SET `utf8`;
USE `rs_chat`;

CREATE TABLE `subjects`
(
	`id`        bigint      NOT NULL AUTO_INCREMENT,
	`name`      varchar(30) NOT NULL,
	`period`    varchar(2)  NOT NULL,
	`type`      char(2)     NOT NULL,
	`credits`   tinyint     NOT NULL,
	`grade`     tinyint     NOT NULL,
	`degree_id` bigint      NOT NULL,

	CONSTRAINT `pk_subject_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_name` UNIQUE (`name`),
	CONSTRAINT `ck_period` CHECK (`period` IN ('A', 'C1', 'C2', 'S1', 'S2')),
	CONSTRAINT `ck_type` CHECK (`type` IN ('TR', 'BO', 'OB', 'OP', 'FB'))
) ENGINE = InnoDB;

CREATE TABLE `degrees`
(
	`id`   bigint AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,

	CONSTRAINT `pk_degree_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_name` UNIQUE (`name`)
) ENGINE InnoDB;

CREATE TABLE `stu_subj`
(
	`subject_id` bigint NOT NULL,
	`student_id` bigint NOT NULL,

	CONSTRAINT `pk_stu_subj` PRIMARY KEY (`subject_id`, `student_id`)
) ENGINE InnoDB;

CREATE TABLE `tea_subj`
(
	`subject_id` bigint NOT NULL,
	`teacher_id` bigint NOT NULL,

	CONSTRAINT `pk_tea_subj` PRIMARY KEY (`subject_id`, `teacher_id`)
) ENGINE InnoDB;

CREATE TABLE `files`
(
	`id`            bigint       NOT NULL AUTO_INCREMENT,
	`name`          varchar(255) NOT NULL,
	`date_uploaded` datetime     NOT NULL,
	`size`          int          NOT NULL,
	`path`          varchar(400),          -- fixme: maybe this column is not needed since the S3 folder is stored in Chat.
	`metadata`      mediumtext,
	`type`          varchar(10)  NOT NULL, -- Text (CUSTOM_JSON) / Image / Audio / Video.
	`user_id`       bigint       NOT NULL,

	CONSTRAINT `pk_file_id` PRIMARY KEY (`id`),
	CONSTRAINT `ck_file_type` CHECK (`type` IN ('TEXT', 'IMAGE', 'AUDIO', 'VIDEO'))
) ENGINE InnoDB;

CREATE TABLE `users`
(
	`id`          bigint       NOT NULL AUTO_INCREMENT,
	`username`    varchar(15)  NOT NULL,
	`password`    varchar(126) NOT NULL, -- Hashed password using Bcrypt and applied Base64 encoding.
	`email`       varchar(70)  NOT NULL,
	`full_name`   varchar(100) NOT NULL,
	`age`         tinyint      NULL,
	`birthdate`   date         NULL,
	`role`        varchar(13)  NOT NULL DEFAULT 'STUDENT',
	`block_until` datetime     NULL,     -- If null, user can login. If date is stored, user cannot login until it has expired.

	CONSTRAINT `pk_user_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_username` UNIQUE (`username`),
	CONSTRAINT `u_email` UNIQUE (`email`),
	CONSTRAINT `ck_username` CHECK ( LENGTH(`username`) BETWEEN 5 AND 15 ),
	CONSTRAINT `ck_email` CHECK ( `email` REGEXP '^[^@]+@[^@]+\.[^@]{2,}$' ),
	CONSTRAINT `ck_age` CHECK ( `age` BETWEEN 13 AND 99 ),
	CONSTRAINT `ck_role` CHECK ( `role` IN ('STUDENT', 'TEACHER', 'ADMINISTRATOR') )
) ENGINE = InnoDB;

CREATE TABLE `sessions`
(
	`id`            bigint       NOT NULL AUTO_INCREMENT,
	`src_ip`        varchar(32)  NOT NULL, -- In case we support IPv6.
	`date_started`  datetime     NOT NULL,
	`access_token`  varchar(300) NOT NULL,
	`refresh_token` varchar(300) NOT NULL,
	`user_id`       bigint       NOT NULL,

	CONSTRAINT `pk_session_id` PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `chats`
(
	`id`        bigint       NOT NULL AUTO_INCREMENT,
	`name`      varchar(100) NOT NULL,
	`type`      varchar(10)  NOT NULL, -- User, Group, Subject, Degree.
	`s3_folder` varchar(300) NULL,
	`metadata`  json         NOT NULL, -- JSON string. Initial value is the creation date.

	CONSTRAINT `pk_chat_id` PRIMARY KEY (`id`),
	CONSTRAINT `ck_chat_type` CHECK (`type` IN ('user', 'group', 'subject', 'degree'))
) ENGINE = InnoDB;

CREATE TABLE `user_chat`
(
	`chat_id` bigint NOT NULL,
	`user_id` bigint NOT NULL,

	CONSTRAINT `pk_user_chat` PRIMARY KEY (`chat_id`, `user_id`)
) ENGINE InnoDB;

CREATE TABLE `user_group`
(
	`group_id` bigint NOT NULL,
	`user_id`  bigint NOT NULL,

	CONSTRAINT `pk_user_group` PRIMARY KEY (`group_id`, `user_id`)
) ENGINE InnoDB;

CREATE TABLE `groups`
(
	`id`   bigint AUTO_INCREMENT,
	`name` varchar(70) NOT NULL, -- Names can be repeated

	CONSTRAINT `pk_group_id` PRIMARY KEY (`id`)
) ENGINE InnoDB;

-- Alter tables

ALTER TABLE `subjects`
	ADD CONSTRAINT `fk_degree_id_subject` FOREIGN KEY (`degree_id`)
		REFERENCES `degrees` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `sessions`
	ADD CONSTRAINT `fk_user_id_session` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `files`
	ADD CONSTRAINT `fk_user_id_file` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `tea_subj`
	ADD CONSTRAINT `fk_subject_id_tea_subj` FOREIGN KEY (`subject_id`)
		REFERENCES `subjects` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `tea_subj`
	ADD CONSTRAINT `fk_teacher_id_tea_subj` FOREIGN KEY (`teacher_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `stu_subj`
	ADD CONSTRAINT `fk_subject_id_stu_subj` FOREIGN KEY (`subject_id`)
		REFERENCES `subjects` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `stu_subj`
	ADD CONSTRAINT `fk_student_id_stu_subj` FOREIGN KEY (`student_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `user_chat`
	ADD CONSTRAINT `fk_chat_id_user_chat` FOREIGN KEY (`chat_id`)
		REFERENCES `chats` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `user_chat`
	ADD CONSTRAINT `fk_user_id_user_chat` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `user_group`
	ADD CONSTRAINT `fk_group_id_user_group` FOREIGN KEY (`group_id`)
		REFERENCES `groups` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `user_group`
	ADD CONSTRAINT `fk_user_id_user_group` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

INSERT INTO `groups` (`name`) VALUE ('global');
INSERT INTO `chats` (`name`, `type`, `s3_folder`, `metadata`) VALUE ('Global', 'group', 'group/Global',
																	 CONCAT('{"createdAt":', ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000), '}'));
