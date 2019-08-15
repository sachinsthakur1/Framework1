SET HUBHOST=localhost
IF NOT [%1] == [] SET HUBHOST=%1
start java -jar selenium-server-standalone-2.51.0.jar -port 4444 -role hub http://%HUBHOST%:4444/grid/register
Exit