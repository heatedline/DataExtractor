CREATE TABLE IF NOT EXISTS `nclat_order_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `companyAppealNo` varchar(45) DEFAULT NULL,
  `dateOfOrder` datetime DEFAULT NULL,
  `party` varchar(255) DEFAULT NULL,
  `section` varchar(45) DEFAULT NULL,
  `courtName` varchar(45) DEFAULT NULL,
  `orderPassedBy` varchar(255) DEFAULT NULL,
  `pdfFileName` varchar(100) DEFAULT NULL,
  `pdfFileSize` varchar(45) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
