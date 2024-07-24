alter table member
    drop foreign key FK41ukictibhnhb6jfpxd1p4h0f;

alter table member
    drop key UKqjf9724fx8tooue4d7oskpave;

alter table member
    add constraint member_client_client_id_fk
        foreign key (client_id) references client (client_id);