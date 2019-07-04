~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Data Extractor Document~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is a simple Selenium WebDriver Java based application which extracts essential data from websites provided and stores them in a MySQL database and also 
downloads PDF files when and where necessary. 

Points to know :

1) Parameterised execution of java class have the parameters "fromDate" and "toDate" specified in the Main.java class, please change accordingly while running the
jar file using the command: 

java -jar -DfromDate="<fromDate>" -DtoDate="<toDate>"

Give the date in dd/MM/yyyy (standard) format, it will be formatted to required formats depnding upon the respective extractors needing that input.

2) Parallel execution of Java classes by creating new thread for each class which will lead to the execution of all the classes simultaneously when the Main.java class 
is executed.

3) Classes that are using the parameters are as follows: 
 a) NCLATDailyCauseListExtractor.java
 b) NCLATTentativeListExtractor.java 
 c) NCLTJudgementsExtractor.java
 d) NCLTOrderExtractor.java
 e) NCLTPublicAnnouncementExtractor.java

4) There is a need to run ChromeDriver on setHeadless(false) mode for functional purposes like changing the window state to another opened window using the 
ChromeDriver which won't work if setHeadless(true) is set on the ChromeOptions. Classes which require to run on setHeadless(false) mode are as follows: 
 a) IBBIOrderExtractor.java
 b) IBBIHighCourtOrderExtractor.java
 c) IBBISupremeCourtOrderExtractor.java
 d) NCLTPublicAnnouncementExtractor.java
 
5) There is a common class named SeleniumBase.java where the ChromeDriver is defined and the ChromeOptions are set and certail WebDriver related common methods are 
written. Each and every Extractor classes extends this SeleniumBase class to use the methods and declarations inside.

6) The PDF Files are downloaded in the Doc folder in the project directory under the respective name of the folder of the Extractors.

7) HikariCP, a “zero-overhead” production-quality connection pool is used for Multiple Connection Pooling. The configuration uses a data source which is maintained 
on db.properties under resources folder.

8) Our database for storing information is MySQL Database, the schema for all the necessary and essential tables are provided under the schema folder in the Project Directory.


# Primary Keys (will be implemented later)
- ibbi_high_court_order, ibbi_order, ibbi_supreme_court_order -> Subject, Date
- nclat_daily_cause_list -> court_name, date
- nclat_daily_tentative_cause_list -> case_no, date
- nclat_judgement -> company_appeal_no, date
- 