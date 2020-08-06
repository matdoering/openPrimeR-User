FROM rocker/shiny:latest
MAINTAINER Matthias Doering <mdoering@mpi-inf.mpg.de>
RUN rm -rf /var/lib/apt/lists/*
# changes: libssl-dev/unstable to libssl-dev because unstable not found (maybe need to change sources?)
# libmysqlclient-dev -> default-libmysqlclient-dev, libgslcblas0 -> libgsl0-dev
RUN apt-get update && apt-get install -y libfontconfig1 libxml2-dev default-jre libssl-dev libv8-dev libgsl0-dev python3-pip pandoc texlive texlive-latex-extra default-libmysqlclient-dev
#    && rm -rf /var/lib/apt/lists/*
RUN pip3 install selenium
WORKDIR /srv/
RUN mkdir primer_design
ADD . primer_design
# install openPrimeR package to install all the dependencies of the tool
RUN R -e 'install.packages("devtools", repos="http://cran.us.r-project.org")'
WORKDIR /srv/primer_design/
RUN Rscript src/install.R FALSE TRUE
# Make all app files readable
# Copy the app to the image
COPY ./ /srv/shiny-server/
RUN chmod -R +r /srv/shiny-server
#update shiny server conf and configure it to run the primer design tool in single app mode
ADD shiny-server.conf /etc/shiny-server/shiny-server.conf
# set ENV vars via bashrc
COPY .docker_bashrc /home/shiny/.bashrc
EXPOSE 3838
COPY shiny-server.sh /usr/bin/shiny-server.sh
#CMD ["/usr/bin/shiny-server"] # doesnt work because jquery has to be set by my script
#RUN chown -R shiny:shiny /srv/
USER shiny
SHELL ["/bin/bash", "-c"]
WORKDIR /home/shiny/
CMD source ~/.bashrc && R -e 'appDir <- system.file("shiny", package = "openPrimeRui"); library(openPrimeR); options("shiny.port"=3838); options(shiny.jquery.version = 1); shiny::runApp(appDir, display.mode = "normal", launch.browser = FALSE, host ="0.0.0.0")'


