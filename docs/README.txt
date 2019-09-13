Nicole Marie Gizzo
661815517

Application Programming in Java- Homework 5
Summer 2019

Farmer's Markets

Creating the User:

  In order create a user, type these commands into the terminal:

  	mysql -u root -p
  
	password
	
	create user 'swagmaster'@'localhost' IDENTIFIED by 'Password123*';

	GRANT ALL PRIVILEGES ON *.* TO 'swagmaster'@'localhost' WITH GRANT OPTION;

	FLUSH PRIVILEGES;

	exit

  Then in terminal navigate to the "sql" folder within the homework5 folder and run the following         commands:
	
	mysql -u swagmaster -p

	Password123*

	source load_fm_data.sql

	source load_zipcode_data.sql

	exit
  

Compilation and Execution Instructions:

  NOTE: I couldn't create my own WAR file in terminal so I used to Eclipse feature
	 which created one for me. 


  In order to compile + run the Farmer's Market website, you must navigate to
  the appropriate directory and then run the "Builer.sh" file and then the
  "Runner.sh" file. Command-line arguments are not required, and the program
  will prompt you for input while it is running.

  After you have run these commands, open up your favorite browser and type this URL into the search bar:

	http://localhost:8080/Homework5/FarmersMarkets/home

Documentation:
  The JavaDoc has already been created and is in the files submitted. However,
  if you would like to run the generation of javadocs on your own, run the
  "JavaDocGen.sh" file.
