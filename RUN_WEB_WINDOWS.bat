@echo off
cd /d "%~dp0"
if exist bin rmdir /s /q bin
mkdir bin
for /r src %%f in (*.java) do echo %%f >> sources.txt
javac -d bin @sources.txt
del sources.txt
java -cp bin web.Server
