@echo off
echo Compiling Bus Ticket Booking System...
javac -cp "lib\mysql-connector-j-9.6.0.jar;." src\*.java -d .

echo Running application...
java -cp "lib\mysql-connector-j-9.6.0.jar;." Main

pause