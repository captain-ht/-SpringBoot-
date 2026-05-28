USE `library_management`;
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `sys_operation_log`;
TRUNCATE TABLE `library_fine`;
TRUNCATE TABLE `library_reservation`;
TRUNCATE TABLE `library_borrow_record`;
TRUNCATE TABLE `library_purchase_order`;
TRUNCATE TABLE `library_book`;
TRUNCATE TABLE `library_reader`;
TRUNCATE TABLE `library_category`;
TRUNCATE TABLE `sys_user`;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role`, `status`) VALUES
(1, 'admin', '$2y$10$L2QUZHbQn3fimXrXH0b58uCC72xprFR9wYIZuc5Viyn0MgceeVSbG', '系统管理员', 'ADMIN', 1),
(2, 'librarian', '$2y$10$L2QUZHbQn3fimXrXH0b58uCC72xprFR9wYIZuc5Viyn0MgceeVSbG', '馆员', 'LIBRARIAN', 1);

INSERT INTO `library_category` (`id`, `name`, `code`, `description`, `sort_order`) VALUES
(1, '计算机', 'CS', '编程语言、架构设计、软件工程与人工智能相关馆藏', 1),
(2, '文学', 'LIT', '中外文学、小说、散文与诗歌作品', 2),
(3, '历史', 'HIS', '历史研究、人物传记与文明史著作', 3),
(4, '管理', 'MGT', '组织管理、项目管理与商业运营图书', 4),
(5, '艺术设计', 'ART', '视觉设计、摄影、建筑与艺术理论', 5);

INSERT INTO `library_book`
(`id`, `book_code`, `isbn`, `title`, `author`, `category_id`, `publisher`, `publish_date`, `price`, `total_stock`, `available_stock`, `borrowed_count`, `status`, `location`, `cover_url`, `description`)
VALUES
(1, 'BK2026001', '9787111128069', 'Java 核心技术 卷 I', 'Cay S. Horstmann', 1, '机械工业出版社', '2024-01-01', 128.00, 12, 8, 26, 1, 'A-01-01', '', '馆内热门编程基础书，适合 Java 入门与进阶。'),
(2, 'BK2026002', '9787302511853', 'Spring Boot 实战', 'Craig Walls', 1, '清华大学出版社', '2024-03-01', 89.00, 10, 6, 21, 1, 'A-01-02', '', 'Web 后端开发高频借阅图书。'),
(3, 'BK2026003', '9787115545381', '深入理解计算机系统', 'Randal E. Bryant', 1, '人民邮电出版社', '2023-09-01', 139.00, 8, 5, 18, 1, 'A-01-03', '', '系统原理课程核心参考书。'),
(4, 'BK2026004', '9787020002207', '活着', '余华', 2, '人民文学出版社', '2012-08-01', 39.00, 15, 12, 9, 1, 'B-02-03', '', '现代文学馆藏常借书目。'),
(5, 'BK2026005', '9787530216835', '百年孤独', '加西亚·马尔克斯', 2, '北京十月文艺出版社', '2017-06-01', 55.00, 9, 7, 11, 1, 'B-02-04', '', '拉丁美洲文学代表作。'),
(6, 'BK2026006', '9787508684031', '人类简史', '尤瓦尔·赫拉利', 3, '中信出版社', '2018-01-01', 68.00, 11, 8, 14, 1, 'C-03-01', '', '历史与文明类热门图书。'),
(7, 'BK2026007', '9787508654072', '原则', 'Ray Dalio', 4, '中信出版社', '2018-04-01', 99.00, 7, 4, 13, 1, 'D-04-02', '', '管理类高借阅图书。'),
(8, 'BK2026008', '9787121456114', '设计中的设计', '原研哉', 5, '电子工业出版社', '2023-05-01', 79.00, 6, 5, 6, 1, 'E-05-01', '', '艺术设计馆藏精品。');

INSERT INTO `library_reader`
(`id`, `reader_no`, `name`, `reader_type`, `gender`, `phone`, `email`, `department`, `register_date`, `max_borrow_count`, `borrow_days`, `status`)
VALUES
(1, 'R2026001', '张三', 1, '男', '13800000001', 'zhangsan@example.com', '信息中心', '2026-05-01', 6, 30, 1),
(2, 'R2026002', '李四', 2, '女', '13800000002', 'lisi@example.com', '读者服务部', '2026-05-10', 4, 20, 1),
(3, 'R2026003', '王敏', 2, '女', '13800000003', 'wangmin@example.com', '软件工程系', '2026-05-12', 5, 25, 1),
(4, 'R2026004', '赵磊', 1, '男', '13800000004', 'zhaolei@example.com', '管理学院', '2026-05-15', 6, 30, 1),
(5, 'R2026005', '陈雨', 2, '女', '13800000005', 'chenyu@example.com', '艺术设计学院', '2026-05-18', 3, 15, 1);

INSERT INTO `library_borrow_record`
(`id`, `reader_id`, `book_id`, `borrow_date`, `due_date`, `return_date`, `overdue_days`, `renew_count`, `status`, `remind_time`)
VALUES
(1, 1, 1, '2026-05-03', '2026-06-02', NULL, 0, 1, 0, NULL),
(2, 2, 2, '2026-05-06', '2026-05-26', NULL, 2, 0, 0, '2026-05-27 08:00:00'),
(3, 3, 6, '2026-05-08', '2026-06-02', '2026-05-25', 0, 0, 1, NULL),
(4, 4, 7, '2026-05-10', '2026-06-09', NULL, 0, 0, 0, NULL),
(5, 5, 4, '2026-05-12', '2026-05-27', '2026-05-27', 0, 0, 1, NULL),
(6, 3, 3, '2026-05-15', '2026-06-09', NULL, 0, 0, 0, NULL),
(7, 1, 5, '2026-05-18', '2026-06-17', NULL, 0, 0, 0, NULL);

INSERT INTO `library_reservation`
(`id`, `reader_id`, `book_id`, `reservation_date`, `expire_date`, `status`)
VALUES
(1, 2, 1, '2026-05-25', '2026-05-28', 0),
(2, 5, 2, '2026-05-23', '2026-05-26', 3),
(3, 4, 8, '2026-05-26', '2026-05-29', 0);

INSERT INTO `library_fine`
(`id`, `borrow_record_id`, `reader_id`, `amount`, `reason`, `status`, `pay_type`, `pay_time`)
VALUES
(1, 2, 2, 1.00, '图书逾期归还', 0, NULL, NULL),
(2, 3, 3, 2.50, '图书逾期归还', 1, '微信', '2026-05-26 10:12:00');

INSERT INTO `library_purchase_order`
(`id`, `purchase_no`, `book_title`, `author`, `isbn`, `publisher`, `quantity`, `unit_price`, `expected_date`, `supplier`, `status`, `remark`)
VALUES
(1, 'PO202605001', '高性能 MySQL', 'Baron Schwartz', '9787121386237', '电子工业出版社', 6, 96.00, '2026-05-30', '新华书店供应链', 1, '数据库课程推荐图书'),
(2, 'PO202605002', '白夜行', '东野圭吾', '9787544291170', '南海出版公司', 8, 49.00, '2026-05-29', '城市图书配送中心', 0, '补充文学类热门借阅书'),
(3, 'PO202605003', '信息可视化设计', '周陟', '9787121459016', '电子工业出版社', 5, 88.00, '2026-05-27', '学术出版服务商', 2, '已完成到货入库');

INSERT INTO `sys_operation_log`
(`id`, `module`, `action`, `username`, `method`, `request_uri`, `ip_address`, `request_data`, `status`, `error_message`, `create_time`)
VALUES
(1, '借阅管理', '借书', 'librarian', 'POST', '/api/borrows/borrow', '127.0.0.1', '[{"readerId":1,"bookId":1}]', 'SUCCESS', NULL, '2026-05-25 09:10:00'),
(2, '借阅管理', '还书', 'librarian', 'POST', '/api/borrows/return', '127.0.0.1', '[{"borrowRecordId":3}]', 'SUCCESS', NULL, '2026-05-26 14:22:00'),
(3, '预约管理', '创建预约', 'admin', 'POST', '/api/reservations', '127.0.0.1', '[{"readerId":4,"bookId":8}]', 'SUCCESS', NULL, '2026-05-26 16:35:00'),
(4, '罚款管理', '缴纳罚款', 'librarian', 'POST', '/api/fines/pay', '127.0.0.1', '[{"fineId":2,"payType":"微信"}]', 'SUCCESS', NULL, '2026-05-26 16:58:00'),
(5, '借阅管理', '续借', 'librarian', 'POST', '/api/borrows/renew', '127.0.0.1', '[{"borrowRecordId":1}]', 'FAILED', '每条借阅记录仅允许续借 1 次', '2026-05-27 11:08:00');

ALTER TABLE `sys_user` AUTO_INCREMENT = 3;
ALTER TABLE `library_category` AUTO_INCREMENT = 6;
ALTER TABLE `library_book` AUTO_INCREMENT = 9;
ALTER TABLE `library_reader` AUTO_INCREMENT = 6;
ALTER TABLE `library_borrow_record` AUTO_INCREMENT = 8;
ALTER TABLE `library_reservation` AUTO_INCREMENT = 4;
ALTER TABLE `library_fine` AUTO_INCREMENT = 3;
ALTER TABLE `library_purchase_order` AUTO_INCREMENT = 4;
ALTER TABLE `sys_operation_log` AUTO_INCREMENT = 6;
