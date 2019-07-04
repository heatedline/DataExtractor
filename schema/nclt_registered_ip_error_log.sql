CREATE TABLE IF NOT EXISTS `nclt_registered_ip_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `regulation` varchar(45) DEFAULT NULL,
  `registrationNumber` varchar(100) DEFAULT NULL,
  `ipName` varchar(255) DEFAULT NULL,
  `ipAddress` varchar(255) DEFAULT NULL,
  `ipEmail` varchar(100) DEFAULT NULL,
  `enrolledWithIPAName` varchar(255) DEFAULT NULL,
  `registrationDate` datetime DEFAULT NULL,
  `remarks` varchar(45) DEFAULT NULL,
  `errorCode` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;