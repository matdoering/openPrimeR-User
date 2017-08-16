#!/usr/bin/env bash
# Start script for the openPrimeR docker container

OS='unknown';
case "$OSTYPE" in
  solaris*) OS="SOLARIS" ;;
  darwin*)  OS="OSX" ;; 
  linux*)   OS="LINUX" ;;
  bsd*)     OS="BSD" ;;
  msys*)    OS="WINDOWS" ;;
  *)        OS="unknown: $OSTYPE" ;;
esac
if [[ $OS -ne "LINUX" || $OS -ne "OSX" ]]; then
    echo "Sorry, your OS is not supported in this script.";
fi
# OS is ok 
# let's check if docker is running already ...
existing=$(docker ps | grep -c 'openPrimeR');
if [ $existing -ne 0 ]; then
    docker stop openPrimeR;
    # wait a bit before-re-executing the docker image
    sleep 2;
fi

# execute docker 
docker run -d --name openPrimeR --rm -p 3838:3838 -v /tmp/logs/:/var/log/shiny-server/ mdoering88/primer_design;
server_location="http://localhost:3838";
sleep 1; # sleep a second to wait for docker to load before we open browser
if [ $OS = "OSX" ]; then
    open $server_location > /dev/null 2>&1;
fi
if [ $OS = "LINUX" ]; then
    xdg-open $server_location > /dev/null 2>&1;
fi
log_file=$(ls -t /tmp/logs/shiny-shiny-*.log | head -1)
tail -F $log_file;
