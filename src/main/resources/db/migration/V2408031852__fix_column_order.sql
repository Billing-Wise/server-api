alter table client
    modify is_deleted bit not null after updated_at;

alter table client
    modify name varchar(50) not null after client_id;

alter table client
    modify auth_code varchar(60) not null after name;

alter table client
    modify phone varchar(20) not null after auth_code;


alter table user
    modify is_deleted bit not null after updated_at;

alter table user
    modify user_id bigint auto_increment first;

alter table user
    modify email varchar(255) not null after client_id;

alter table user
    modify password varchar(60) not null after email;

alter table user
    modify name varchar(50) not null after password;

alter table user
    modify phone varchar(20) not null after name;


alter table contract
    modify contract_cycle int not null after item_amount;

alter table contract
    modify is_deleted bit not null after updated_at;

alter table contract
    modify is_easy_consent bit not null after payment_due_cycle;

alter table contract
    modify contract_id bigint auto_increment first;

alter table contract
    modify item_price bigint not null after is_subscription;

alter table contract
    modify member_id bigint not null after contract_id;

alter table contract
    modify item_id bigint not null after member_id;

alter table contract
    modify invoice_type_id bigint not null after item_id;

alter table contract
    modify contract_status_id bigint not null after invoice_type_id;

alter table contract
    modify payment_type_id bigint not null after contract_status_id;


alter table member
    modify client_id bigint not null after member_id;

alter table member
    modify phone varchar(20) not null after email;

alter table member
    modify created_at datetime(6) not null after phone;

alter table member
    modify updated_at datetime(6) not null after created_at;

alter table member
    modify is_deleted bit not null after updated_at;


alter table consent_account
    modify created_at datetime(6) not null after sign_url;

alter table consent_account
    modify updated_at datetime(6) not null after created_at;

alter table consent_account
    modify is_deleted bit not null after updated_at;

alter table consent_account
    modify bank varchar(50) not null after owner;

alter table consent_account
    modify number varchar(20) not null after bank;


alter table item
    modify is_deleted bit not null after updated_at;

alter table item
    modify client_id bigint not null after item_id;

alter table item
    modify name varchar(50) not null after client_id;

alter table item
    modify image_url varchar(512) not null after price;

alter table item
    modify is_basic bit not null after image_url;

alter table item
    modify created_at datetime(6) not null after is_basic;

alter table item
    modify description varchar(255) not null after name;


alter table contract_status
    modify is_deleted bit not null after updated_at;

alter table contract_status
    modify name varchar(50) not null after contract_status_id;


alter table payment_type
    modify created_at datetime(6) not null after is_basic;

alter table payment_type
    modify updated_at datetime(6) not null after created_at;

alter table payment_type
    modify is_deleted bit not null after updated_at;


alter table invoice
    modify is_deleted bit not null after updated_at;

alter table invoice
    modify contract_id bigint not null after invoice_id;

alter table invoice
    modify payment_status_id bigint not null after payment_type_id;

alter table invoice
    modify charge_amount bigint not null after payment_status_id;

alter table invoice
    modify contract_date datetime(6) not null after charge_amount;

alter table invoice
    modify due_date datetime(6) not null after contract_date;

alter table invoice
    modify created_at datetime(6) not null after due_date;


alter table payment_status
    modify is_deleted bit not null after updated_at;

alter table payment_status
    modify name varchar(50) not null after payment_status_id;

alter table payment_status
    modify created_at datetime(6) not null after name;


alter table invoice_type
    modify is_deleted bit not null after updated_at;

alter table invoice_type
    modify name varchar(50) not null after invoice_type_id;

alter table invoice_type
    modify created_at datetime(6) not null after name;


alter table payment
    modify is_deleted bit not null after updated_at;

alter table payment
    modify created_at datetime(6) not null after pay_amount;

alter table payment
    modify payment_method varchar(50) not null after invoice_id;


alter table payment_account
    modify number varchar(20) not null after owner;

alter table payment_account
    modify created_at datetime(6) not null after number;

alter table payment_account
    modify updated_at datetime(6) not null after created_at;

alter table payment_account
    modify is_deleted bit not null after updated_at;


alter table payment_card
    modify created_at datetime(6) not null after owner;

alter table payment_card
    modify updated_at datetime(6) not null after created_at;

alter table payment_card
    modify is_deleted bit not null after updated_at;


alter table invoice_statistics
    modify client_id bigint not null after id;

alter table invoice_statistics
    modify type_id bigint not null after client_id;

alter table invoice_statistics
    modify is_deleted tinyint(1) default 0 not null after updated_at;

alter table invoice_statistics
    modify reference_date timestamp not null after week;


alter table invoice_statistics_type
    modify is_deleted tinyint(1) default 0 not null after updated_at;