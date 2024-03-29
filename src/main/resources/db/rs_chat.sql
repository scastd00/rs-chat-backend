DROP DATABASE IF EXISTS `rs_chat`;
CREATE DATABASE IF NOT EXISTS `rs_chat` DEFAULT CHARSET = `utf8mb4`;
USE `rs_chat`;

CREATE TABLE `subjects`
(
	`id`        bigint      NOT NULL AUTO_INCREMENT,
	`name`      varchar(70) NOT NULL, -- Not unique because a subject can be in different degrees
	`period`    varchar(2)  NOT NULL,
	`type`      char(2)     NOT NULL,
	`credits`   tinyint     NOT NULL,
	`grade`     tinyint     NOT NULL,
	`degree_id` bigint      NOT NULL,

	CONSTRAINT `pk_subject_id` PRIMARY KEY (`id`),
	CONSTRAINT `ck_period` CHECK (`period` IN ('A', 'C1', 'C2', 'S1', 'S2')),
	CONSTRAINT `ck_type` CHECK (`type` IN ('TR', 'BO', 'OB', 'OP', 'FB'))
) ENGINE = InnoDB;

CREATE TABLE `degrees`
(
	`id`   bigint       NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,

	CONSTRAINT `pk_degree_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_name` UNIQUE (`name`)
) ENGINE InnoDB;

CREATE TABLE `stu_subj`
(
	`student_id` bigint NOT NULL,
	`subject_id` bigint NOT NULL,

	CONSTRAINT `pk_stu_subj` PRIMARY KEY (`student_id`, `subject_id`)
) ENGINE InnoDB;

CREATE TABLE `tea_subj`
(
	`teacher_id` bigint NOT NULL,
	`subject_id` bigint NOT NULL,

	CONSTRAINT `pk_tea_subj` PRIMARY KEY (`teacher_id`, `subject_id`)
) ENGINE InnoDB;

CREATE TABLE `files`
(
	`id`            bigint       NOT NULL AUTO_INCREMENT,
	`name`          varchar(255) NOT NULL,
	`date_uploaded` datetime     NOT NULL,
	`size`          int          NOT NULL,
	`path`          varchar(400) NOT NULL,
	`metadata`      json         NOT NULL,
	`type`          varchar(20)  NOT NULL, -- text / image / audio / video / application
	`user_id`       bigint       NOT NULL,

	CONSTRAINT `pk_file_id` PRIMARY KEY (`id`),
	CONSTRAINT `ck_file_type` CHECK (`type` IN ('text', 'image', 'audio', 'video', 'application')) # The MIME type
) ENGINE InnoDB;

CREATE TABLE `users`
(
	`id`                    bigint       NOT NULL AUTO_INCREMENT,
	`username`              varchar(15)  NOT NULL,
	`password`              varchar(126) NOT NULL,              -- Hashed password using Bcrypt.
	`email`                 varchar(70)  NOT NULL,
	`full_name`             varchar(100) NOT NULL,
	`age`                   tinyint      NULL     DEFAULT NULL,
	`birthdate`             date         NULL     DEFAULT NULL,
	`role`                  varchar(13)  NOT NULL DEFAULT 'STUDENT',
	`block_until`           datetime     NULL     DEFAULT NULL, -- If null, user can login. If date is stored, user cannot login until it has expired.
	`password_code`         varchar(6)   NULL     DEFAULT NULL, -- Code used to reset password.
	`message_count_by_type` json         NOT NULL,
	`nsfw_count`            tinyint      NOT NULL DEFAULT 0,    -- Number of times the user has uploaded NSFW content.

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
	`id`         bigint       NOT NULL AUTO_INCREMENT,
	`src_ip`     varchar(32)  NOT NULL, -- In case we support IPv6.
	`start_date` datetime     NOT NULL,
	`end_date`   datetime     NOT NULL,
	`token`      varchar(300) NOT NULL,
	`user_id`    bigint       NOT NULL,

	CONSTRAINT `pk_session_id` PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `chats`
(
	`id`              bigint       NOT NULL AUTO_INCREMENT,
	`name`            varchar(100) NOT NULL,
	`type`            varchar(10)  NOT NULL, -- User, Group, Subject, Degree.
	`s3_folder`       varchar(300) NOT NULL,
	`metadata`        json         NOT NULL, -- Initial value is the creation date.
	`invitation_code` char(15)     NOT NULL, -- Random string of 15 characters.
	`key`             varchar(30)  NOT NULL,

	CONSTRAINT `pk_chat_id` PRIMARY KEY (`id`),
	CONSTRAINT `ck_chat_type` CHECK (`type` IN ('user', 'group', 'subject', 'degree')),
	CONSTRAINT `u_invitation_code` UNIQUE (`invitation_code`),
	CONSTRAINT `u_key` UNIQUE (`key`)
) ENGINE = InnoDB;

CREATE TABLE `user_chat`
(
	`user_id` bigint NOT NULL,
	`chat_id` bigint NOT NULL,

	CONSTRAINT `pk_user_chat` PRIMARY KEY (`user_id`, `chat_id`)
) ENGINE InnoDB;

CREATE TABLE `user_group`
(
	`user_id`  bigint NOT NULL,
	`group_id` bigint NOT NULL,

	CONSTRAINT `pk_user_group` PRIMARY KEY (`user_id`, `group_id`)
) ENGINE InnoDB;

CREATE TABLE `groups`
(
	`id`   bigint      NOT NULL AUTO_INCREMENT,
	`name` varchar(70) NOT NULL, -- Names can be repeated

	CONSTRAINT `pk_group_id` PRIMARY KEY (`id`)
) ENGINE InnoDB;

CREATE TABLE `emojis`
(
	`id`          bigint         NOT NULL AUTO_INCREMENT,
	`name`        varchar(100)   NOT NULL,
	`icon`        varbinary(100) NOT NULL,
	`unicode`     varchar(80)    NOT NULL,
	`category`    varchar(30)    NOT NULL,
	`subcategory` varchar(40)    NOT NULL,

	CONSTRAINT `pk_emoji_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_name` UNIQUE (`name`),
	CONSTRAINT `u_unicode` UNIQUE (`unicode`)
) ENGINE InnoDB;

CREATE TABLE `badges`
(
	`id`             bigint       NOT NULL AUTO_INCREMENT,
	`title`          varchar(100) NOT NULL,
	`description`    varchar(300) NOT NULL,
	`icon`           varchar(200) NOT NULL,
	`type`           varchar(20)  NOT NULL,
	`points_of_type` int          NOT NULL,

	CONSTRAINT `pk_badge_id` PRIMARY KEY (`id`),
	CONSTRAINT `u_title` UNIQUE (`title`)
) ENGINE InnoDB;

CREATE TABLE `user_badge`
(
	`user_id`  bigint NOT NULL,
	`badge_id` bigint NOT NULL,

	CONSTRAINT `pk_user_badge` PRIMARY KEY (`user_id`, `badge_id`)
) ENGINE InnoDB;

create table `friends`
(
	`user_id`   bigint   NOT NULL,
	`friend_id` bigint   NOT NULL,
	`since`     datetime NOT NULL,

	CONSTRAINT `pk_friends` PRIMARY KEY (`user_id`, `friend_id`)
) ENGINE InnoDB;

create table `blocked_users`
(
	`user_id`    bigint   NOT NULL,
	`blocked_id` bigint   NOT NULL,
	`since`      datetime NOT NULL,

	CONSTRAINT `pk_blocked_users` PRIMARY KEY (`user_id`, `blocked_id`)
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

ALTER TABLE `user_badge`
	ADD CONSTRAINT `fk_user_id_user_badge` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `user_badge`
	ADD CONSTRAINT `fk_badge_id_user_badge` FOREIGN KEY (`badge_id`)
		REFERENCES `badges` (`id`)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE `friends`
	ADD CONSTRAINT `fk_user_id_friends` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE CASCADE; -- If a user is deleted, all his friends are deleted

ALTER TABLE `friends`
	ADD CONSTRAINT `fk_friend_id_friends` FOREIGN KEY (`friend_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE CASCADE;

ALTER TABLE `blocked_users`
	ADD CONSTRAINT `fk_user_id_blocked_users` FOREIGN KEY (`user_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE CASCADE; -- If a user is deleted, all his blocked users are deleted

ALTER TABLE `blocked_users`
	ADD CONSTRAINT `fk_blocked_id_blocked_users` FOREIGN KEY (`blocked_id`)
		REFERENCES `users` (`id`)
		ON UPDATE CASCADE
		ON DELETE CASCADE;
