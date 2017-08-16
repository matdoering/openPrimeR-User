FROM rocker/shiny:latest
MAINTAINER Matthias Doering <mdoering@mpi-inf.mpg.de>
# R library dependencies considered in this dockerfile:
    #shinyjs, cshapes, countrycode, Biostrings, XML,, scales, gplots, reshape2, seqinr, IRanges, ggplot2, plyr, stringdist, stringr, doParallel, gtools, RColorBrewer, sets, DECIPHER, digest, shiny, DT, shinyBS, lpSolve,lpSolveAPI
# Python dependencies considered:
    # selenium
#install system packages
# libxml2 (required by R XML library), java runtime environment (required by MELTING), libssl-dev/unstable required by openssl package, libgsl0ldbl: required for viennaRNA,
# python-pip is necessary to install selenium for python (retrieval of template seqs)
# ensure that apt-get update works 
# pandoc (rmarkdown reports) requires latex for PDF output
RUN rm -rf /var/lib/apt/lists/*
RUN apt-get update && apt-get install -y libfontconfig1 libxml2-dev default-jre libssl-dev/unstable libv8-dev libgsl2 python-pip pandoc texlive texlive-latex-extra
#    && rm -rf /var/lib/apt/lists/*
RUN pip install selenium
# switch to root for permissions to create folders
#USER root # markus doesn't use this?
# set to user to shiny (the user that starts the server) such that dependencies are installed in the right folder (rocker uses root user by default ..)
#USER shiny
WORKDIR /srv/
RUN mkdir primer_design
ADD . primer_design
# install openPrimeR package to install all the dependencies of the tool
RUN Rscript primer_design/src/install.R FALSE TRUE
#update shiny server conf and configure it to run the primer design tool in single app mode
ADD shiny-server.conf /etc/shiny-server/shiny-server.conf
COPY .docker_bashrc /home/shiny/.bashrc
# modify the shiny user (defined in shiny-server.conf) bashrc to have the modified path we set when running the shiny server
# change owner of primer_design folder in order to be able to write there with the shiny user
RUN chown -R shiny:shiny primer_design/
WORKDIR /srv/primer_design
#USER shiny # keep running as root, otherwise we have problem with bookmark_state_dir
