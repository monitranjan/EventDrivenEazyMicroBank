CREATE TABLE IF NOT EXISTS `customer` (
  `customer_id` varchar(100)  PRIMARY KEY,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mobile_number` varchar(20) NOT NULL,
  `active_sw` boolean NOT NULL default 0,
  `created_at` date NOT NULL,
  `created_by` varchar(20) NOT NULL,
  `updated_at` date DEFAULT NULL,
    `updated_by` varchar(20) DEFAULT NULL
);