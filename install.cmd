@Echo off
REM Batch script for installing openPrimeR on Windows. Ensures that all ENV variables are defined.
SET oldpath=%PATH%
if "%~1" equ ":main" (
  shift /1
  goto main
)
REM CLEANUP TAKS GO HERE (reset path)
cmd /d /c "%~f0" :main %*
SET PATH=%oldpath%
EXIT /b
REM CLEANUP TASKS END HERE

REM ACTUAL SCRIPT GOES HERE
:main
SET mypath=%~dp0
SET dir=%mypath%tools
SET PATH=%PATH%;%dir%\oligoarrayaux\bin\;%dir%\MAFFT\;%dir%\viennaRNA\;%dir%\MELTING\executable\;%dir%\phantomjs\bin\;%dir%\Pandoc\
SET UNAFOLDDAT=%dir%\oligoarrayaux\share\oligoarrayaux\
REM set MELTING location of 'custom' nearest neighbor folders (here we set to the default, to make it work for WIN)
SET NN_PATH=%dir%\MELTING\Data\
REM Check whether R exists in the PATH. Use `where` to produce an ERRORLEVEl.
where /Q Rscript
IF ERRORLEVEL 1 (
    ECHO Couldn't find R in your PATH. Please ensure that R is installed and present in your PATH.
    EXIT /B
) ELSE (
	Rscript %mypath%src/install.R
)
PAUSE