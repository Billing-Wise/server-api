CREATE TABLE invoice_statistics_type (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         type_name VARCHAR(50) NOT NULL,
                                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE invoice_statistics (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    reference_date TIMESTAMP NOT NULL,
                                    total_invoiced BIGINT NOT NULL,
                                    total_collected BIGINT NOT NULL,
                                    outstanding BIGINT NOT NULL,
                                    type_id BIGINT NOT NULL,
                                    year INT NOT NULL,
                                    month INT DEFAULT NULL,
                                    week INT DEFAULT NULL,
                                    client_id BIGINT NOT NULL,
                                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    FOREIGN KEY (type_id) REFERENCES invoice_statistics_type(id)
);
