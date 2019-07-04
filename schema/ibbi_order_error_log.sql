CREATE TABLE IF NOT EXISTS `ibbi_order_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `dateOfOrder` datetime DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `pdfFileName` varchar(255) DEFAULT NULL,
  `orderRemarks` varchar(100) DEFAULT NULL,
  `reason` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
