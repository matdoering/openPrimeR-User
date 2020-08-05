#!/usr/bin/env Rscript 

# script for locally starting the primer design app for users
# -> installs the tool if necesary
######
getScriptPath <- function() {
    cmd.args <- commandArgs()
    m <- regexpr("(?<=^--file=).+", cmd.args, perl = TRUE)
    script.dir <- dirname(regmatches(cmd.args, m))
    if (length(script.dir) == 0) 
        stop("can't determine script dir: please call the script with Rscript")
    if (length(script.dir) > 1) 
        stop("can't determine script dir: more than one '--file' argument detected")
    # if spaces are in script dir, they are replaced with '~+~'
    script.dir <- gsub("~\\+~", " ", script.dir)
    return(script.dir)
}
this.file.name <- function() {
    frame_files <- lapply(sys.frames(), function(x) x$ofile)
    frame_files <- Filter(Negate(is.null), frame_files)
    frame_files[[length(frame_files)]]
}
library(methods) # for rscript calls
#########
# set working directory to directory of openPrimer package
path <- try(getScriptPath(), silent=TRUE) # for Rscript call of start.R
if (class(path) == "try-error") { # for non-Rscript calls
    path <- try(dirname(this.file.name()), silent = TRUE)
}
if (class(path) == "try-error") { # other cases
    path <- getwd() # assume we're in the right directory ('src')
} 
openPrimeR.path <- file.path(path, "openPrimeR") # path to openPrimeR package
openPrimeR.ui.path <- file.path(path, "openPrimeRui") # path to openPrimeR frontend
#source(file.path(path, "inst", "shiny", "shiny_server", "extra_install_helper.R"))
# load current pkg version from source (no library call here such that changes to the source are directly reflected in the tool).
devtools::load_all(openPrimeR.path, export_all = FALSE)
devtools::load_all(openPrimeR.ui.path, export_all = FALSE)
#if (class(test) == "try-error") {
    #print(attr(test, "condition"))
    #message("openPrimeR couldn't be loaded by devtools.\n",
            #"Check your package dependencies.")
#}
#requireNamespace("openPrimeR", quietly = TRUE)
# don't install tools, some users might be happy with what they have or have problems with installing ...
message("Starting the app ...")
# start the shiny app:
openPrimeRui::startApp()
#####################################3
###################
# useful comands:
####################
# options(shiny.trace = TRUE)
# BIOCLITE INSTALL
# source("https://bioconductor.org/biocLite.R")
# biocLite(lib = "/local/home/mdoering/R/x86_64-pc-linux-gnu-library/3.3", lib.loc = "/local/home/mdoering/R/x86_64-pc-linux-gnu-library/3.3")
# 
# devtools::load_all()
# devtools::document()
# devtools::check() # automatically builds vignettes
# R CMD check --no-build-vignettes --no-manual openPrimeR
# devtools::run_examples() # only run examples
# devtools::test() # do all unit tests
# R CMD Rd2pdf src/openPrimeR # create pkg manual with all functions in man/ folder
# devtools::build_vignettes()
# BiocCheck("./") # BioConductor additional checks
# render rmarkdown html:
# rmarkdown::render("src/openPrimeR/vignettes/openPrimeR_vignette.Rmd")
# rmarkdown::render("src/openPrimeR/inst/tutorials/introduction/introduction.Rmd")

# learnr::run_tutorial("introduction", package = "openPrimeR")
####
# RELEASE TESTS
######
# R CMD check
# R CMD build
# R CMD BiocCheck <pkg>
