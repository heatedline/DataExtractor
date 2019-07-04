CREATE TABLE IF NOT EXISTS `nclt_announcement_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `announcementDesc` varchar(100) DEFAULT NULL,
  `dateOfAnnouncement` datetime DEFAULT NULL,
  `lastDateOfSubmission` datetime DEFAULT NULL,
  `corporateDebtorName` varchar(100) DEFAULT NULL,
  `applicantName` varchar(100) DEFAULT NULL,
  `insolvencyProfessionalName` varchar(100) DEFAULT NULL,
  `insolvencyProfessionalAddress` varchar(255) DEFAULT NULL,
  `pdfFileName` varchar(100) DEFAULT NULL,
  `remark` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;