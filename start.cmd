@Echo off
REM Batch script for starting openPrimeR in Windows. Ensures that all ENV variables are defined.
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
	Rscript %mypath%src/start.R
)
PAUSE