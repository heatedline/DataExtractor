CREATE TABLE IF NOT EXISTS `nclt_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `caseNo` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `petitionerVsRespondent` varchar(255) DEFAULT NULL,
  `listingDate` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
