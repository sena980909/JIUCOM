-- V4: Remove duplicate indexes (covered by composite indexes)

-- posts: idx_posts_board (board_type) is covered by idx_post_board_deleted (board_type, is_deleted, created_at)
DROP INDEX idx_posts_board ON posts;

-- comments: idx_comments_post (post_id) is covered by idx_comment_post_parent (post_id, parent_id, is_deleted)
DROP INDEX idx_comments_post ON comments;

-- reviews: idx_reviews_part (part_id) is covered by idx_review_part (part_id, is_deleted)
DROP INDEX idx_reviews_part ON reviews;

-- notifications: idx_notifications_user (user_id) is covered by idx_notification_user (user_id, is_read, is_deleted)
DROP INDEX idx_notifications_user ON notifications;
