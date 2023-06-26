# wikipedia automation test
[Test URL][https://en.wikipedia.org/wiki/Main_Page]

This project is a testing challenge project. Part 1 is manual testcase wrote on an excel templace, part 2 is API automation test using postman, part 3 is UI automation test using Selenium Java
## Table of Contents
- Installation and usage for Part 1
- Installation and usage for Part 2
- Installation for Part 3
- Usage for Part 3
## Installation and usage for Part 1
1. In the project structure,navigate to `Part 1 test` directory and open the excel file inside it
2. The excel file contains my test for part 1, the first Excel sheet is to check the testing report, the remaining sheets is basically the same but check for different browsers
## Installation and usage for Part 2
1. Download Postman  here [Postman Download](https://www.postman.com/downloads/)
2. In the project structure,navigate to `Part 2 test` directory and download json file inside it
3. Open postman, select "Collections" tab
4. Click "import" button and import the downloaded json file
5. My collection should be displayed inside "Collection" tab
6. Open the GET API in the collection, inside "Tests" tab is my test script
7. To run the test, simply click "Send" button to run the API, the test result will be displayed in "Test results" tab below

## Installation for part 3 

To run the UI automation tests using Selenium and Java with TestNG, follow these steps:

1. Make sure you have Java Development Kit (JDK) 11 installed on your system. You can download the JDK from the Oracle website: [Java SE Downloads](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

2. Install IntelliJ IDEA, an integrated development environment (IDE) for Java. You can download IntelliJ IDEA Community Edition from the JetBrains website: [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/).

3. Clone this repository https://github.com/HoangNhatTran/smg-test
4. Open IntelliJ IDEA and import the cloned project by selecting "Import Project" and choosing the project directory.

## Usage for part 3

To run the UI automation tests using this project, follow these steps:

1. Open IntelliJ and ensure the project is loaded.

2. Navigate to the `src/test/java/Wikipedia` directory in the project structure.

3. There are 2 testcases in this test class

4. To run the test, open terminal, type "mvn test" then enter

6. A recording of the test will be added to C:\Users\ < user name> \Videos folder

8. Repeat steps 4 whenever you want to run the UI automation tests again