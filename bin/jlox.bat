@ECHO OFF
set MYPATH=%~dp0
set CLASSPATH=%CLASSPATH%;%MYPATH%..\out\production\lox

%JAVA_HOME%\bin\java com.craftinginterpreters.lox.Lox %*