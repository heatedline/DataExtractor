CREATE TABLE IF NOT EXISTS `ibbi_supreme_court_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `dateOfOrder` datetime DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `pdfFileName` varchar(255) DEFAULT NULL,
  `orderRemarks` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;