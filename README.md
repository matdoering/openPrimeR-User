# openPrimeR
## Synopsis

openPrimeR is an R package providing methods for designing, evaluating,and
comparing primer sets for multiplex polymerase chain reaction (PCR). The package provides a primer design
function that generates novel primer setes by solving a
set cover problem such that the number of covered template sequences is
maximized with the smallest possible set of primers. Moreover, existing primer sets can be evaluated
according to their coverage and their fulfillment of constraints on the
PCR-relevant physicochemical properties. For PCR tasks for which multiple
possible primer sets exist, openPrimeR can facilitate the selection of the
most suitable set by performing comparative analyses. The R package includes a Shiny application that
provides a comprehensive and intuitive user interface for the core functionalites of the package.

## Using openPrimeR

We provide two distributions for openPrimeR. The R package (including the Shiny app) can be retrieved using this GitHub repository. Additionally, we provide a self-contained Docker image for the Shiny application. The openPrimeR Docker image contains all of the dependencies of the tool, which makes it easily usable on any type of system, independent of the installed operating system, R distribution, etc. We recommend that you use the Docker image if you belong to the following groups of users:

1. You do not have any experience with R whatsoever and you do not intend to use the functionalities of the R package.
2. You only want to use the openPrimeR frontend in terms of the Shiny app.
3. You do not want to invest time in installing the package with all of its dependencies.


## Docker Image

The openPrimeR docker image is available at [Docker Hub](https://hub.docker.com/repository/docker/mdoering88/openprimer). Further information about the use of the image can be found through the provided link.


## Installation from GitHub

In a console, enter

>git clone https://github.com/matdoering/openPrimeR-User
>git submodule update --init --recursive**

To update your local version of the tool to the current GitHub version at a later point in time, just run

>git pull origin master
>git submodule update --remote

in the project's base folder. 

In case that you have performed changes to the local files and you would like to revert to the last GitHub version, execute

>git checkout -- .

in the project folder in order to discard all local changes.

### Installing the tools: MacOS and Unix

In a console, enter the project's base folder and execute

>./install.sh

### Installing the tools: Windows

If you are using Windows, please execute the batch script

>./install.cmd

Please ensure that you have added R into your system's path before using the script. For more information, on changing the path, please refer to the [R FAQ](https://cran.r-project.org/bin/windows/base/rw-FAQ.html#Rcmd-is-not-found-in-my-PATH_0021).

Note that Windows installer may currently not work.

### Starting the tool

If successful, the install routine automatically starts the openPrimeR user interface.

After successful installation, you can work with openPrimeR like this:

```
library(openPrimeR) # to work with the openPrimeR API
library(openPrimeRui)
startApp() # start the UI
```

Note that you need to set your path according to the installation directory of the 3rd party tools beforehand, i.e.

```
export PATH=$PATH:"$dir/tools/oligoarrayaux/bin/:$dir/tools/MAFFT/bin/:$dir/tools/ViennaRNA/bin/:$dir/tools/MELTING/executable/:$dir/tools/phantomjs/bin/:$dir/tools/pandoc/";
export UNAFOLDDAT="$dir/tools/oligoarrayaux/share/oligoarrayaux/";
```

where `$dir` should be set to the installation directory of `openPrimeR-User`.

## Contributors

openPrimeR is being developed at the Max Planck Institute for Informatics and the University of Cologne.

## License

See the [LICENSE](LICENSE.txt) file for license rights and limitations (GNU General Public License, Version 2.0).

