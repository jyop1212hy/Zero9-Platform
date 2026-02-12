CREATE INDEX idx_gpp_status_start
    ON group_purchase_posts (gpp_progress_status, start_date);

CREATE INDEX idx_gpp_status_end
    ON group_purchase_posts (gpp_progress_status, end_date);