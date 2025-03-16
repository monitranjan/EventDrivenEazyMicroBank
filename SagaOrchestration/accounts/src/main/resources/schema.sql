CREATE TABLE IF NOT EXISTS `accounts` (
   `account_number` int AUTO_INCREMENT  PRIMARY KEY,
   `mobile_number` varchar(20) NOT NULL,
   `account_type` varchar(100) NOT NULL,
   `branch_address` varchar(200) NOT NULL,
   `active_sw` boolean NOT NULL default 0,
   `created_at` date NOT NULL,
   `created_by` varchar(20) NOT NULL,
   `updated_at` date DEFAULT NULL,
   `updated_by` varchar(20) DEFAULT NULL
);