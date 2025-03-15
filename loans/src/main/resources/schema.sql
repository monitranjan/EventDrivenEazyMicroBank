CREATE TABLE IF NOT EXISTS `loans` (
  `loan_number` int NOT NULL  PRIMARY KEY,
  `mobile_number` varchar(20) NOT NULL,
  `loan_type` varchar(100) NOT NULL,
  `total_loan` int NOT NULL,
  `amount_paid` int NOT NULL,
  `outstanding_amount` int NOT NULL,
  `active_sw` boolean NOT NULL default 0,
  `created_at` date NOT NULL,
  `created_by` varchar(20) NOT NULL,
  `updated_at` date DEFAULT NULL,
  `updated_by` varchar(20) DEFAULT NULL
);