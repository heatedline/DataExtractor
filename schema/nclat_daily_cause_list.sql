CREATE TABLE IF NOT EXISTS `nclat_daily_cause_list` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `courtName` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `pdfFileName` varchar(255) DEFAULT NULL,
  `pdfSize` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
