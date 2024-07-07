create table client (
                        is_deleted bit not null,
                        client_id bigint not null auto_increment,
                        created_at datetime(6) not null,
                        updated_at datetime(6) not null,
                        phone varchar(20) not null,
                        name varchar(50) not null,
                        auth_code varchar(60) not null,
                        primary key (client_id)
) engine=InnoDB;

create table consent_account (
                                 is_deleted bit not null,
                                 created_at datetime(6) not null,
                                 member_id bigint not null,
                                 updated_at datetime(6) not null,
                                 number varchar(20) not null,
                                 bank varchar(50) not null,
                                 owner varchar(50) not null,
                                 sign_url varchar(512) not null,
                                 primary key (member_id)
) engine=InnoDB;

create table contract (
                          contract_cycle integer not null,
                          is_deleted bit not null,
                          is_easy_consent bit not null,
                          is_subscription bit not null,
                          item_amount integer not null,
                          payment_due_cycle integer not null,
                          contract_id bigint not null auto_increment,
                          contract_status_id bigint not null,
                          created_at datetime(6) not null,
                          invoice_type_id bigint not null,
                          item_id bigint not null,
                          item_price bigint not null,
                          member_id bigint not null,
                          payment_type_id bigint not null,
                          updated_at datetime(6) not null,
                          primary key (contract_id)
) engine=InnoDB;

create table contract_status (
                                 is_deleted bit not null,
                                 contract_status_id bigint not null auto_increment,
                                 created_at datetime(6) not null,
                                 updated_at datetime(6) not null,
                                 name varchar(50) not null,
                                 primary key (contract_status_id)
) engine=InnoDB;

create table invoice (
                         is_deleted bit not null,
                         charge_amount bigint not null,
                         contract_date datetime(6) not null,
                         contract_id bigint not null,
                         created_at datetime(6) not null,
                         due_date datetime(6) not null,
                         invoice_id bigint not null auto_increment,
                         invoice_type_id bigint not null,
                         payment_status_id bigint not null,
                         payment_type_id bigint not null,
                         updated_at datetime(6) not null,
                         primary key (invoice_id)
) engine=InnoDB;

create table invoice_type (
                              is_deleted bit not null,
                              created_at datetime(6) not null,
                              invoice_type_id bigint not null auto_increment,
                              updated_at datetime(6) not null,
                              name varchar(50) not null,
                              primary key (invoice_type_id)
) engine=InnoDB;

create table item (
                      is_basic bit not null,
                      is_deleted bit not null,
                      client_id bigint not null,
                      created_at datetime(6) not null,
                      item_id bigint not null auto_increment,
                      price bigint not null,
                      updated_at datetime(6) not null,
                      name varchar(50) not null,
                      image_url varchar(512) not null,
                      description varchar(255) not null,
                      primary key (item_id)
) engine=InnoDB;

create table member (
                        is_deleted bit not null,
                        client_id bigint not null,
                        created_at datetime(6) not null,
                        member_id bigint not null auto_increment,
                        updated_at datetime(6) not null,
                        phone varchar(20) not null,
                        name varchar(50) not null,
                        description varchar(255) not null,
                        email varchar(255) not null,
                        primary key (member_id)
) engine=InnoDB;

create table payment (
                         is_deleted bit not null,
                         created_at datetime(6) not null,
                         invoice_id bigint not null,
                         pay_amount bigint not null,
                         updated_at datetime(6) not null,
                         payment_method varchar(50) not null,
                         primary key (invoice_id)
) engine=InnoDB;

create table payment_account (
                                 is_deleted bit not null,
                                 created_at datetime(6) not null,
                                 invoice_id bigint not null,
                                 updated_at datetime(6) not null,
                                 number varchar(20) not null,
                                 bank varchar(50) not null,
                                 owner varchar(50) not null,
                                 primary key (invoice_id)
) engine=InnoDB;

create table payment_card (
                              is_deleted bit not null,
                              created_at datetime(6) not null,
                              invoice_id bigint not null,
                              updated_at datetime(6) not null,
                              number varchar(20) not null,
                              company varchar(50) not null,
                              owner varchar(50) not null,
                              primary key (invoice_id)
) engine=InnoDB;

create table payment_status (
                                is_deleted bit not null,
                                created_at datetime(6) not null,
                                payment_status_id bigint not null auto_increment,
                                updated_at datetime(6) not null,
                                name varchar(50) not null,
                                primary key (payment_status_id)
) engine=InnoDB;

create table payment_type (
                              is_deleted bit not null,
                              created_at datetime(6) not null,
                              payment_type_id bigint not null auto_increment,
                              updated_at datetime(6) not null,
                              name varchar(50) not null,
                              primary key (payment_type_id)
) engine=InnoDB;

create table user (
                      is_deleted bit not null,
                      client_id bigint not null,
                      created_at datetime(6) not null,
                      updated_at datetime(6) not null,
                      user_id bigint not null auto_increment,
                      phone varchar(20) not null,
                      name varchar(50) not null,
                      password varchar(60) not null,
                      email varchar(255) not null,
                      primary key (user_id)
) engine=InnoDB;

alter table client
    add constraint UK_n76e2d9hm60cmeaq98thb80m1 unique (auth_code);

alter table member
    add constraint UKqjf9724fx8tooue4d7oskpave unique (client_id, email);

alter table user
    add constraint UK_ob8kqyqqgmefl0aco34akdtpe unique (email);

alter table consent_account
    add constraint FKxpan67vhouoxco50c2f7q4mm
        foreign key (member_id)
            references member (member_id);

alter table contract
    add constraint FKes1tgaq9gjebt7qt5cbcycwnx
        foreign key (contract_status_id)
            references contract_status (contract_status_id);

alter table contract
    add constraint FKypycpg9ahav4yf1vrb7027t0
        foreign key (invoice_type_id)
            references invoice_type (invoice_type_id);

alter table contract
    add constraint FKm502k9yuthnok2500wt8o00vc
        foreign key (item_id)
            references item (item_id);

alter table contract
    add constraint FKpc7kwq3a6sixvv90l2osreiay
        foreign key (member_id)
            references member (member_id);

alter table contract
    add constraint FKjjv1yjkms4nr7u6g6rq8eeayl
        foreign key (payment_type_id)
            references payment_type (payment_type_id);

alter table invoice
    add constraint FKqh9ibaacfusht7an2afwkrq5
        foreign key (contract_id)
            references contract (contract_id);

alter table invoice
    add constraint FKqss90tikrowtmcc9gw6hegq7d
        foreign key (invoice_type_id)
            references invoice_type (invoice_type_id);

alter table invoice
    add constraint FKog843k0kkikrh4o8gbtk1iqa0
        foreign key (payment_status_id)
            references payment_status (payment_status_id);

alter table invoice
    add constraint FK57h4dyeb290661nx2xmsl4wsg
        foreign key (payment_type_id)
            references payment_type (payment_type_id);

alter table item
    add constraint FKpxnjeqy0c2uq7xqdt6t76flj6
        foreign key (client_id)
            references client (client_id);

alter table member
    add constraint FK41ukictibhnhb6jfpxd1p4h0f
        foreign key (client_id)
            references client (client_id);

alter table payment
    add constraint FKsb24p8f52refbb80qwp4gem9n
        foreign key (invoice_id)
            references invoice (invoice_id);

alter table payment_account
    add constraint FKpfxw0x5fd2e05o9r3lqedfpka
        foreign key (invoice_id)
            references payment (invoice_id);

alter table payment_card
    add constraint FKgy9fcm0144exrxwvs0c20i211
        foreign key (invoice_id)
            references payment (invoice_id);

alter table user
    add constraint FKrl8au09hfjd9742b89li2rb3
        foreign key (client_id)
            references client (client_id);