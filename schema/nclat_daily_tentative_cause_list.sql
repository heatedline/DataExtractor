CREATE TABLE IF NOT EXISTS `nclat_daily_tentative_cause_list` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `caseNo` varchar(255) DEFAULT NULL,
  `partyName` varchar(255) DEFAULT NULL,
  `section` varchar(45) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
