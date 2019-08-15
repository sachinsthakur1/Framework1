HUBHOST=localhost
NODEHOST=localhost
java -jar selenium-server-standalone-2.48.2.jar -role node -hub http://$HUBHOST:4444/grid/register -hubHost $HUBHOST -host $NODEHOST -port 5555 -browser browserName=firefox -browser browserName=safari -browser browserName=chrome -Dwebdriver.chrome.driver=chromedriver
