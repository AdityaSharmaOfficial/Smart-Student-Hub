@echo off
echo === Smart Sphere Student Hub - Build ===
if not exist out mkdir out
javac -d out ^
  src\model\*.java ^
  src\filehandler\*.java ^
  src\service\*.java ^
  src\ui\*.java ^
  src\main\*.java
if %errorlevel%==0 (
  echo === Build SUCCESS ===
  echo Run: java -cp out main.Main
) else (
  echo === Build FAILED ===
)
