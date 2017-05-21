@echo off
setlocal

rem
rem Copyright (c) 1999, 2006 Tanuki Software Inc.
rem
rem Permission is hereby granted, free of charge, to any person
rem obtaining a copy of the Java Service Wrapper and associated
rem documentation files (the "Software"), to deal in the Software
rem without  restriction, including without limitation the rights
rem to use, copy, modify, merge, publish, distribute, sub-license,
rem and/or sell copies of the Software, and to permit persons to
rem whom the Software is furnished to do so, subject to the
rem following conditions:
rem
rem The above copyright notice and this permission notice shall be
rem included in all copies or substantial portions of the Software.
rem
rem THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
rem EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
rem OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
rem NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
rem HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
rem WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
rem FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
rem OTHER DEALINGS IN THE SOFTWARE.

rem
rem Java Service Wrapper script.  Suitable for starting and stopping
rem  wrapped Java applications on Windows platforms.
rem
rem This file is originally from Java Service Wrapper 3.2.3 distribution
rem with alteration to fit the needs of AppAssembler Maven Plugin
rem

if "%OS%"=="Windows_NT" goto nt
echo This script only works with NT-based versions of Windows.
exit /b 1

:nt

set BASEDIR=%~dp0\..


rem
rem Find the application home.
rem
rem %~dp0 is location of current script under NT
set _REALPATH=%~dp0

rem Decide on the wrapper binary.
set _WRAPPER_BASE=wrapper

if "%PROCESSOR_ARCHITEW6432%"=="AMD64" goto amd64
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto amd64
if "%PROCESSOR_ARCHITECTURE%"=="IA64" goto ia64

set _WRAPPER_EXE=%_REALPATH%%_WRAPPER_BASE%-windows-x86-32.exe
goto validate_wrapper_exe

:amd64
set _WRAPPER_EXE=%_REALPATH%%_WRAPPER_BASE%-windows-x86-64.exe
goto validate_wrapper_exe

:ia64
set _WRAPPER_EXE=%_REALPATH%%_WRAPPER_BASE%-windows-ia-64.exe
goto validate_wrapper_exe

:validate_wrapper_exe
if NOT exist "%_WRAPPER_EXE%" set _WRAPPER_EXE=%_REALPATH%%_WRAPPER_BASE%.exe

if exist "%_WRAPPER_EXE%" goto validate

echo Unable to locate a Wrapper executable using any of the following names:
echo %_REALPATH%%_WRAPPER_BASE%-windows-x86-32.exe
echo %_REALPATH%%_WRAPPER_BASE%-windows-x86-64.exe
echo %_REALPATH%%_WRAPPER_BASE%.exe
exit /b 1

:validate
rem Find the requested command.
for /F %%v in ('echo %1^|findstr "^console$ ^start$ ^pause$ ^resume$ ^stop$ ^restart$ ^install$ ^remove ^status"') do call :exec set COMMAND=%%v

if "%COMMAND%" == "" (
    echo Usage: %0 { console : start : pause : resume : stop : restart : install : remove : status }
    exit /b 1
) else (
    shift
)

rem
rem Find the wrapper.conf
rem
:conf
set _WRAPPER_CONF="%_REALPATH%..\webRoot\wrapper.conf"

rem
rem Run the application.
rem
call :%COMMAND%
goto :eof

:console
"%_WRAPPER_EXE%" -c %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:start
"%_WRAPPER_EXE%" -t %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:pause
"%_WRAPPER_EXE%" -a %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:resume
"%_WRAPPER_EXE%" -e %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:stop
"%_WRAPPER_EXE%" -p %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:install
"%_WRAPPER_EXE%" -i %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:remove
"%_WRAPPER_EXE%" -r %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:status
"%_WRAPPER_EXE%" -q %_WRAPPER_CONF% %WRAPPER_CONF_OVERRIDES%
goto :eof

:restart
call :stop
call :start
goto :eof

:exec
%*
goto :eof

