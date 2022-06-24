DROP DATABASE IF EXISTS ule_chat;
CREATE DATABASE IF NOT EXISTS ule_chat CHARACTER SET utf8;
USE ule_chat;

CREATE TABLE subjects
(
	id          bigint      NOT NULL AUTO_INCREMENT,
	name        varchar(30) NOT NULL,
	subj_period varchar(2)  NOT NULL,
	type        char(2)     NOT NULL,
	credits     tinyint     NOT NULL,
	grade       tinyint     NOT NULL,
	degree_id   bigint      NOT NULL,

	CONSTRAINT pk_subject_id PRIMARY KEY (id),
	CONSTRAINT u_name UNIQUE (name),
	CONSTRAINT ck_period CHECK (subj_period IN ('A', 'C1', 'C2', 'S1', 'S2')),
	CONSTRAINT ck_type CHECK (type IN ('TR', 'BO', 'OB', 'OP', 'FB'))
) ENGINE = InnoDB;

CREATE TABLE degrees
(
	id   bigint AUTO_INCREMENT,
	name varchar(255) NOT NULL,

	CONSTRAINT pk_degree_id PRIMARY KEY (id),
	CONSTRAINT u_name UNIQUE (name)
) ENGINE InnoDB;

CREATE TABLE stu_subj
(
	subject_id bigint NOT NULL,
	student_id bigint NOT NULL,

	CONSTRAINT pk_stu_subj PRIMARY KEY (subject_id, student_id)
) ENGINE InnoDB;

CREATE TABLE tea_subj
(
	subject_id bigint NOT NULL,
	teacher_id bigint NOT NULL,

	CONSTRAINT pk_tea_subj PRIMARY KEY (subject_id, teacher_id)
) ENGINE InnoDB;

CREATE TABLE files
(
	id            bigint       NOT NULL AUTO_INCREMENT,
	name          varchar(255) NOT NULL,
	date_uploaded datetime     NOT NULL,
	size          int          NOT NULL,
	path          varchar(400),
	metadata      varchar(700),
	type          varchar(10)  NOT NULL,
	user_id       bigint       NOT NULL,

	CONSTRAINT pk_file_id PRIMARY KEY (id)
) ENGINE InnoDB;

CREATE TABLE users
(
	id          bigint       NOT NULL AUTO_INCREMENT,
	username    varchar(15)  NOT NULL,
	password    varchar(126) NOT NULL,              -- Hashed password using Bcrypt and applied Base64 encoding.
	email       varchar(70)  NOT NULL,
	full_name   varchar(100) NOT NULL,
	age         tinyint      NULL,
	birthdate   date         NULL,
	role        varchar(13)  NOT NULL DEFAULT 'STUDENT',
	block_until datetime     NULL     DEFAULT NULL, -- If null, user can login. If date is stored, user cannot login until it has expired.

	CONSTRAINT pk_user_id PRIMARY KEY (id),
	CONSTRAINT u_username UNIQUE (username),
	CONSTRAINT u_email UNIQUE (email),
	CONSTRAINT ck_username CHECK ( LENGTH(username) BETWEEN 5 AND 15 ),
	CONSTRAINT ck_email CHECK ( email REGEXP '^[^@]+@[^@]+\.[^@]{2,}$' ),
	CONSTRAINT ck_age CHECK ( age BETWEEN 13 AND 99 ),
	CONSTRAINT ck_role CHECK ( role IN ('STUDENT', 'TEACHER', 'ADMINISTRATOR') )
) ENGINE = InnoDB;

CREATE TABLE sessions
(
	id            bigint       NOT NULL AUTO_INCREMENT,
	src_ip        varchar(32)  NOT NULL, -- In case we support IPv6
	date_started  datetime     NOT NULL,
	access_token  varchar(300) NOT NULL,
	refresh_token varchar(300) NOT NULL,
	user_id       bigint       NOT NULL,

	CONSTRAINT pk_session_id PRIMARY KEY (id)
) ENGINE = InnoDB;

-- File must be one of the following (ISA relationship) -> Image, Audio, Video.

-- Alter tables

ALTER TABLE subjects
	ADD CONSTRAINT fk_degree_id_subject FOREIGN KEY (degree_id)
		REFERENCES degrees (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE sessions
	ADD CONSTRAINT fk_user_id_session FOREIGN KEY (user_id)
		REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE files
	ADD CONSTRAINT fk_user_id_file FOREIGN KEY (user_id)
		REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE tea_subj
	ADD CONSTRAINT fk_subject_id_tea_subj FOREIGN KEY (subject_id)
		REFERENCES subjects (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE tea_subj
	ADD CONSTRAINT fk_teacher_id_tea_subj FOREIGN KEY (teacher_id)
		REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE stu_subj
	ADD CONSTRAINT fk_subject_id_stu_subj FOREIGN KEY (subject_id)
		REFERENCES subjects (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE stu_subj
	ADD CONSTRAINT fk_student_id_stu_subj FOREIGN KEY (student_id)
		REFERENCES users (id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

