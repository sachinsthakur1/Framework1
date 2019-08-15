SET HUBHOST=localhost

start java -jar selenium-server-standalone-2.51.0.jar -role webdriver -hub http://%HUBHOST%:4444/grid/register -Dwebdriver.chrome.driver=chromedriver.exe -Dwebdriver.ie.driver=IEDriverServer.exe
Exit