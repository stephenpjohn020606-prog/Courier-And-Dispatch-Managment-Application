@echo off
cd /d "%~dp0"
java "-Ddb.host=mysql-26b6709d-exluffy451-8b3d.j.aivencloud.com" "-Ddb.port=16720" "-Ddb.name=defaultdb" "-Ddb.user=avnadmin" "-Ddb.password=TYPEPASSWORDHERE" -cp "compiled files;libraries/*" courier.Main
pause
