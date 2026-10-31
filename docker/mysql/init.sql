CREATE DATABASE account_db;
CREATE DATABASE inventory_db;
CREATE DATABASE shipping_db;

CREATE USER 'account_user'@'%' IDENTIFIED BY 'account_pw';
CREATE USER 'inventory_user'@'%' IDENTIFIED BY 'inventory_pw';
CREATE USER 'shipping_user'@'%' IDENTIFIED BY 'shipping_pw';

GRANT ALL PRIVILEGES ON account_db.* TO 'account_user'@'%';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'inventory_user'@'%';
GRANT ALL PRIVILEGES ON shipping_db.* TO 'shipping_user'@'%';

CREATE USER 'debezium'@'%' IDENTIFIED BY 'debezium_pw';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium'@'%';
