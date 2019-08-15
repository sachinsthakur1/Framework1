HUBHOST=localhost
java -jar selenium-server-standalone-2.48.2.jar -role hub http://$HUBHOST:4444/grid/register
