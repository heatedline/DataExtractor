CREATE TABLE IF NOT EXISTS `nclt_interim_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `caseNo` varchar(45) DEFAULT NULL,
  `dateOfOrder` datetime DEFAULT NULL,
  `pdfFileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
