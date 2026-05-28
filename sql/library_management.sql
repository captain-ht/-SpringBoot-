USE `library_management`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `sys_operation_log`;
DROP TABLE IF EXISTS `library_fine`;
DROP TABLE IF EXISTS `library_reservation`;
DROP TABLE IF EXISTS `library_borrow_record`;
DROP TABLE IF EXISTS `library_purchase_order`;
DROP TABLE IF EXISTS `library_book`;
DROP TABLE IF EXISTS `library_reader`;
DROP TABLE IF EXISTS `library_category`;
DROP TABLE IF EXISTS `sys_user`;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `real_name` VARCHAR(50) DEFAULT NULL,
  `role` VARCHAR(20) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `module` VARCHAR(50) NOT NULL,
  `action` VARCHAR(100) NOT NULL,
  `username` VARCHAR(50) DEFAULT NULL,
  `method` VARCHAR(20) DEFAULT NULL,
  `request_uri` VARCHAR(255) DEFAULT NULL,
  `ip_address` VARCHAR(64) DEFAULT NULL,
  `request_data` TEXT,
  `status` VARCHAR(20) DEFAULT NULL,
  `error_message` VARCHAR(500) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(50) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `sort_order` INT DEFAULT 0,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `book_code` VARCHAR(50) NOT NULL,
  `isbn` VARCHAR(50) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `author` VARCHAR(100) NOT NULL,
  `category_id` BIGINT NOT NULL,
  `publisher` VARCHAR(100) DEFAULT NULL,
  `publish_date` DATE DEFAULT NULL,
  `price` DECIMAL(10,2) DEFAULT NULL,
  `total_stock` INT NOT NULL DEFAULT 0,
  `available_stock` INT NOT NULL DEFAULT 0,
  `borrowed_count` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `location` VARCHAR(100) DEFAULT NULL,
  `cover_url` VARCHAR(255) DEFAULT NULL,
  `description` TEXT,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_code` (`book_code`),
  UNIQUE KEY `uk_isbn` (`isbn`),
  KEY `idx_book_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_reader` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reader_no` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `reader_type` TINYINT NOT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `department` VARCHAR(100) DEFAULT NULL,
  `register_date` DATE DEFAULT NULL,
  `max_borrow_count` INT NOT NULL DEFAULT 5,
  `borrow_days` INT NOT NULL DEFAULT 30,
  `status` TINYINT NOT NULL DEFAULT 1,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reader_no` (`reader_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reader_id` BIGINT NOT NULL,
  `book_id` BIGINT NOT NULL,
  `borrow_date` DATE NOT NULL,
  `due_date` DATE NOT NULL,
  `return_date` DATE DEFAULT NULL,
  `overdue_days` INT DEFAULT 0,
  `renew_count` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0借阅中 1已归还',
  `remind_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_borrow_reader_id` (`reader_id`),
  KEY `idx_borrow_book_id` (`book_id`),
  KEY `idx_borrow_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `reader_id` BIGINT NOT NULL,
  `book_id` BIGINT NOT NULL,
  `reservation_date` DATE NOT NULL,
  `expire_date` DATE NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0预约中 1已完成 2已取消 3已失效',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reservation_reader_id` (`reader_id`),
  KEY `idx_reservation_book_id` (`book_id`),
  KEY `idx_reservation_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_fine` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `borrow_record_id` BIGINT NOT NULL,
  `reader_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `reason` VARCHAR(255) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0未支付 1已支付',
  `pay_type` VARCHAR(30) DEFAULT NULL,
  `pay_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_fine_reader_id` (`reader_id`),
  KEY `idx_fine_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `library_purchase_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `purchase_no` VARCHAR(50) NOT NULL,
  `book_title` VARCHAR(200) NOT NULL,
  `author` VARCHAR(100) NOT NULL,
  `isbn` VARCHAR(50) DEFAULT NULL,
  `publisher` VARCHAR(100) DEFAULT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `unit_price` DECIMAL(10,2) DEFAULT NULL,
  `expected_date` DATE DEFAULT NULL,
  `supplier` VARCHAR(100) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0待采购 1已下单 2已到货',
  `remark` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_purchase_no` (`purchase_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
