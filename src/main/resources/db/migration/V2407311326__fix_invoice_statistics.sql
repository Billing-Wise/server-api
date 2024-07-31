alter table invoice_statistics
    add constraint invoice_statistics_client_client_id_fk
        foreign key (client_id) references client (client_id);

INSERT INTO invoice_statistics_type (type_name, is_deleted)
VALUES
    ('주간', false),
    ('월간', false);