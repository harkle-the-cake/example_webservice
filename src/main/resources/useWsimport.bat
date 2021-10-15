PATH=%PATH%;C:\Program Files\Java\jdk1.8.0_91\bin
REM freie Web-Services
REM wsimport -s src -p eu.boxwork.dhbw.gen.geoipservice http://www.webservicex.net/geoipservice.asmx?WSDL
REM Web-Service fuer Vorlesung
wsimport -d src -keep -p eu.boxwork.dhbw.gen.webservices http://192.168.178.200:8080/services?wsdl
pause