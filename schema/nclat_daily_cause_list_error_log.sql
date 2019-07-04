CREATE TABLE IF NOT EXISTS `nclat_daily_cause_list_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `courtName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date` date DEFAULT NULL,
  `pdfFileName` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pdfSize` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
