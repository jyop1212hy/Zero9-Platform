CREATE TABLE notifications (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               group_purchase_post_id BIGINT NOT NULL,
                               title VARCHAR(100) NOT NULL,
                               content VARCHAR(255) NOT NULL,
                               is_read BIT(1) NOT NULL DEFAULT b'0',
                               created_at DATETIME(6) DEFAULT NULL,
                               updated_at DATETIME(6) DEFAULT NULL,
                               PRIMARY KEY (id)
);