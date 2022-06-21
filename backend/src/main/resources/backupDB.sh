#!/bin/bash

mysqldump -h remote-databases.comkwjnhybv4.eu-west-3.rds.amazonaws.com \
	-u admin_LM_IS1 \
	-pLM_pass_IS1 \
	--port=3306 \
	--single-transaction \
	--routines \
	--triggers \
	--databases ule_chat > rds-dump.sql

sed -e '/INSERT/,/!/!d' < rds-dump.sql > inserts.sql
grep 'INSERT' inserts.sql > data.sql
rm rds-dump.sql
rm inserts.sql
