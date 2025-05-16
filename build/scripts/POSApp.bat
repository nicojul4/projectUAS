@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  POSApp startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and POS_APP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\POSApp-1.0.jar;%APP_HOME%\lib\hibernate-core-6.4.4.Final.jar;%APP_HOME%\lib\itextpdf-5.5.13.3.jar;%APP_HOME%\lib\poi-ooxml-5.2.5.jar;%APP_HOME%\lib\poi-5.2.5.jar;%APP_HOME%\lib\gson-2.10.1.jar;%APP_HOME%\lib\jakarta.persistence-api-3.1.0.jar;%APP_HOME%\lib\junit-4.13.2.jar;%APP_HOME%\lib\javafx-fxml-21-linux.jar;%APP_HOME%\lib\javafx-controls-21-linux.jar;%APP_HOME%\lib\javafx-controls-21.jar;%APP_HOME%\lib\javafx-graphics-21-linux.jar;%APP_HOME%\lib\javafx-graphics-21.jar;%APP_HOME%\lib\javafx-base-21-linux.jar;%APP_HOME%\lib\javafx-base-21.jar;%APP_HOME%\lib\mysql-connector-j-8.0.33.jar;%APP_HOME%\lib\jakarta.transaction-api-2.0.1.jar;%APP_HOME%\lib\jboss-logging-3.5.0.Final.jar;%APP_HOME%\lib\hibernate-commons-annotations-6.0.6.Final.jar;%APP_HOME%\lib\jandex-3.1.2.jar;%APP_HOME%\lib\classmate-1.5.1.jar;%APP_HOME%\lib\byte-buddy-1.14.11.jar;%APP_HOME%\lib\jaxb-runtime-4.0.2.jar;%APP_HOME%\lib\jaxb-core-4.0.2.jar;%APP_HOME%\lib\jakarta.xml.bind-api-4.0.0.jar;%APP_HOME%\lib\jakarta.inject-api-2.0.1.jar;%APP_HOME%\lib\antlr4-runtime-4.13.0.jar;%APP_HOME%\lib\barcodes-7.1.14.jar;%APP_HOME%\lib\font-asian-7.1.14.jar;%APP_HOME%\lib\sign-7.1.14.jar;%APP_HOME%\lib\forms-7.1.14.jar;%APP_HOME%\lib\hyph-7.1.14.jar;%APP_HOME%\lib\svg-7.1.14.jar;%APP_HOME%\lib\styled-xml-parser-7.1.14.jar;%APP_HOME%\lib\layout-7.1.14.jar;%APP_HOME%\lib\pdfa-7.1.14.jar;%APP_HOME%\lib\kernel-7.1.14.jar;%APP_HOME%\lib\io-7.1.14.jar;%APP_HOME%\lib\commons-codec-1.16.0.jar;%APP_HOME%\lib\commons-collections4-4.4.jar;%APP_HOME%\lib\commons-math3-3.6.1.jar;%APP_HOME%\lib\commons-io-2.15.0.jar;%APP_HOME%\lib\SparseBitSet-1.3.jar;%APP_HOME%\lib\poi-ooxml-lite-5.2.5.jar;%APP_HOME%\lib\xmlbeans-5.2.0.jar;%APP_HOME%\lib\log4j-api-2.21.1.jar;%APP_HOME%\lib\commons-compress-1.25.0.jar;%APP_HOME%\lib\curvesapi-1.08.jar;%APP_HOME%\lib\hamcrest-core-1.3.jar;%APP_HOME%\lib\protobuf-java-3.21.9.jar;%APP_HOME%\lib\angus-activation-2.0.0.jar;%APP_HOME%\lib\jakarta.activation-api-2.1.1.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.67.jar;%APP_HOME%\lib\bcprov-jdk15on-1.67.jar;%APP_HOME%\lib\txw2-4.0.2.jar;%APP_HOME%\lib\istack-commons-runtime-4.1.1.jar


@rem Execute POSApp
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %POS_APP_OPTS%  -classpath "%CLASSPATH%" edu.pradita.p14.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable POS_APP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%POS_APP_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
